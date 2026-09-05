package com.fire.mangareader.presentation.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.presentation.reader.viewer.ReaderColorFilterView;
import com.fire.mangareader.presentation.reader.viewer.WebtoonAdapter;
import com.fire.mangareader.presentation.reader.viewer.WebtoonRecyclerView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChapterReaderActivity extends AppCompatActivity {

    private android.view.View eyeFilterOverlay;
    private com.google.android.material.floatingactionbutton.FloatingActionButton fabSettings;
    private int currentFilterMode = 0;

    private android.widget.TextView tvReadingTimer;
    private long readingStartTime;
    private android.os.Handler timerHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - readingStartTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            if (tvReadingTimer != null) {
                tvReadingTimer.setText(String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds));
            }
            timerHandler.postDelayed(this, 1000);
        }
    };


    private WebtoonRecyclerView recyclerView;
    private WebtoonAdapter adapter;
    private ReaderColorFilterView colorFilterView;
    private FrameLayout uiOverlay;
    private TextView tvPageIndicator, tvChapterTitle;
    private ImageView btnBack, btnEyeFilter;
    private ProgressBar loadingProgressBar;
    private WebView scraperWebView; 
    private LinearLayoutManager layoutManager;
    
    private SeekBar pageSeekBar;
    private TextView tvCurrentPageSeek, tvTotalPagesSeek;
    private ImageButton btnNextChapter, btnPreviousChapter;

    private SeekBar brightnessSeekBar;
    private boolean isUiVisible = false;
    private boolean isHorizontalMode = false;
    private androidx.recyclerview.widget.PagerSnapHelper snapHelper = new androidx.recyclerview.widget.PagerSnapHelper();
    private boolean isFilterActive = false;
    
    private String chapterUrl;
    private String mangaUrl;
    private String chapterTitle;
    
    private String nextChapterUrl = null, nextChapterTitle = null;
    private String prevChapterUrl = null, prevChapterTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        com.fire.mangareader.util.DisplayUtils.optimizeRefreshRate(this);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 28) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        
        setContentView(R.layout.activity_chapter_reader);

        tvReadingTimer = findViewById(R.id.tvReadingTimer);
        readingStartTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 1000);


        chapterUrl = getIntent().getStringExtra("chapterUrl");
        mangaUrl = getIntent().getStringExtra("mangaUrl");
        chapterTitle = getIntent().getStringExtra("chapterTitle");
        String mangaTitle = getIntent().getStringExtra("mangaTitle");
        String mangaCover = getIntent().getStringExtra("mangaCover");

        if (mangaTitle != null && mangaCover != null) {
            com.fire.mangareader.util.RecentReadingManager.RecentItem item = new com.fire.mangareader.util.RecentReadingManager.RecentItem();
            item.mangaUrl = mangaUrl;
            item.mangaTitle = mangaTitle;
            item.mangaCover = mangaCover;
            item.chapterUrl = chapterUrl;
            item.chapterTitle = chapterTitle;
            com.fire.mangareader.util.RecentReadingManager.addRecent(this, item);
        }

        recyclerView = findViewById(R.id.recyclerView);

        eyeFilterOverlay = findViewById(R.id.eyeFilterOverlay);
        fabSettings = findViewById(R.id.fabSettings);
        
        // Hide/Show FAB on scroll (optional, but good for UX)
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fabSettings.isShown()) {
                    fabSettings.hide();
                } else if (dy < 0 && !fabSettings.isShown()) {
                    fabSettings.show();
                }
            }
        });
        
        fabSettings.setOnClickListener(v -> showReaderSettings());

        colorFilterView = findViewById(R.id.colorFilterView);
        uiOverlay = findViewById(R.id.uiOverlay);
        tvPageIndicator = findViewById(R.id.tvPageIndicator);
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        btnBack = findViewById(R.id.btnBack);
        btnEyeFilter = findViewById(R.id.btnEyeFilter);
        scraperWebView = findViewById(R.id.scraperWebView); if(scraperWebView != null) scraperWebView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);

        pageSeekBar = findViewById(R.id.pageSeekBar);
        tvCurrentPageSeek = findViewById(R.id.tvCurrentPageSeek);
        tvTotalPagesSeek = findViewById(R.id.tvTotalPagesSeek);
        btnNextChapter = findViewById(R.id.btnNextChapter);
        btnPreviousChapter = findViewById(R.id.btnPreviousChapter);

        if (chapterTitle != null) tvChapterTitle.setText(chapterTitle);

        loadingProgressBar = new ProgressBar(this);
        brightnessSeekBar = findViewById(R.id.brightnessSeekBar);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                android.view.WindowManager.LayoutParams lp = getWindow().getAttributes();
                float brightness = progress / 255.0f;
                if (brightness < 0.05f) brightness = 0.05f; // Prevent completely black screen
                lp.screenBrightness = brightness;
                getWindow().setAttributes(lp);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        FrameLayout.LayoutParams pbParams = new FrameLayout.LayoutParams(150, 150, android.view.Gravity.CENTER);
        loadingProgressBar.setLayoutParams(pbParams);
        ((FrameLayout) findViewById(android.R.id.content)).addView(loadingProgressBar);

        hideSystemUI();

        // 🚀 إعدادات تسريع التنقل والتحميل المسبق
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setItemPrefetchEnabled(true); // تفعيل التحميل المسبق
        layoutManager.setInitialPrefetchItemCount(5); // تحميل 5 صفحات قادمة في الخلفية
        
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemViewCacheSize(10); // الاحتفاظ بـ 10 صفحات سابقة في الرام لمنع التقطيع
        // recyclerView.setHasFixedSize(true); // تسريع الرسم

        adapter = new WebtoonAdapter();
        recyclerView.setAdapter(adapter);

        loadChapterListFromDb();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem != RecyclerView.NO_POSITION) {
                    int currentPage = firstVisibleItem + 1;
                    int totalPages = adapter.getItemCount();
                    
                    tvPageIndicator.setText(currentPage + " / " + totalPages);
                    tvCurrentPageSeek.setText(String.valueOf(currentPage));
                    pageSeekBar.setOnSeekBarChangeListener(null); 
                    pageSeekBar.setProgress(firstVisibleItem);
                    setupSeekBarListener(); 
                    
                    saveReadingProgress(currentPage, totalPages);
                }
            }
        });
        
        setupSeekBarListener(); 

        btnNextChapter.setOnClickListener(v -> {
            if (nextChapterUrl != null) {
                openNewChapter(nextChapterUrl, nextChapterTitle);
            } else {
                Toast.makeText(this, "أنت تقرأ الفصل الأخير حالياً!", Toast.LENGTH_SHORT).show();
            }
        });

        btnPreviousChapter.setOnClickListener(v -> {
            if (prevChapterUrl != null) {
                openNewChapter(prevChapterUrl, prevChapterTitle);
            } else {
                Toast.makeText(this, "هذا هو الفصل الأول!", Toast.LENGTH_SHORT).show();
            }
        });


        android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int readingMode = prefs.getInt("reading_mode", 0); // 0=Vert, 1=LTR, 2=RTL
        if (readingMode == 1) {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            snapHelper.attachToRecyclerView(recyclerView);
        } else if (readingMode == 2) {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
            snapHelper.attachToRecyclerView(recyclerView);
        } else {
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }
        layoutManager.setItemPrefetchEnabled(true);
        layoutManager.setInitialPrefetchItemCount(5);
        recyclerView.setLayoutManager(layoutManager);

        ImageView btnToggleDirection = findViewById(R.id.btnToggleDirection);
        btnToggleDirection.setOnClickListener(v -> {
            String[] options = {"عمودي (ويب تون)", "أفقي (من اليسار لليمين)", "أفقي (من اليمين لليسار - مانجا)"};
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("اختر وضع القراءة")
                .setItems(options, (dialog, which) -> {
                    androidx.preference.PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("reading_mode", which).apply();
                    if (which == 0) { // Vertical
                        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                        btnToggleDirection.setColorFilter(android.graphics.Color.WHITE);
                        Toast.makeText(this, "وضع القراءة: عمودي", Toast.LENGTH_SHORT).show();
                    } else if (which == 1) { // Horizontal LTR
                        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        btnToggleDirection.setColorFilter(android.graphics.Color.GREEN);
                        Toast.makeText(this, "وضع القراءة: أفقي (LTR)", Toast.LENGTH_SHORT).show();
                    } else if (which == 2) { // Horizontal RTL
                        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true); // true for reverse layout
                        btnToggleDirection.setColorFilter(android.graphics.Color.GREEN);
                        Toast.makeText(this, "وضع القراءة: مانجا يابانية (RTL)", Toast.LENGTH_SHORT).show();
                    }
                    
                    layoutManager.setItemPrefetchEnabled(true);
                    layoutManager.setInitialPrefetchItemCount(5);
                    recyclerView.setLayoutManager(layoutManager);
                    
                    if (which != 0) {
                        recyclerView.setOnFlingListener(null);
                        snapHelper.attachToRecyclerView(recyclerView);
                    } else {
                        recyclerView.setOnFlingListener(null);
                    }

                    if (adapter != null && !tvPageIndicator.getText().toString().isEmpty()) {
                        try {
                            int pos = Integer.parseInt(tvPageIndicator.getText().toString().split("/")[0].trim()) - 1;
                            if (pos >= 0) layoutManager.scrollToPosition(pos);
                        } catch (Exception e) {}
                    }
                })
                .show();
        });


        btnEyeFilter.setOnClickListener(v -> {
            isFilterActive = !isFilterActive;
            if (isFilterActive) {
                colorFilterView.setVisibility(View.VISIBLE);
                colorFilterView.setFilter(Color.argb(80, 200, 150, 100), 5); 
                btnEyeFilter.setColorFilter(Color.GREEN);
            } else {
                colorFilterView.setVisibility(View.GONE);
                colorFilterView.setFilter(Color.TRANSPARENT, 0);
                btnEyeFilter.setColorFilter(Color.WHITE);
            }
        });

        recyclerView.setOnClickListener(v -> toggleUi());
        btnBack.setOnClickListener(v -> onBackPressed());

        checkIfChapterIsDownloadedAndLoad();
    }
    private void showReaderSettings() {
        String[] options = {"بدون فلتر", "تظليل", "دافيء (حماية العين)", "ليلي قوي"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("إعدادات القراءة")
            .setSingleChoiceItems(options, currentFilterMode, (dialog, which) -> {
                currentFilterMode = which;
                if (which == 0) {
                    eyeFilterOverlay.setBackgroundColor(android.graphics.Color.TRANSPARENT);
                } else if (which == 1) {
                    eyeFilterOverlay.setBackgroundColor(android.graphics.Color.parseColor("#66000000")); // Black 40%
                } else if (which == 2) {
                    eyeFilterOverlay.setBackgroundColor(android.graphics.Color.parseColor("#33FF9800")); // Warm Orange 20%
                } else if (which == 3) {
                    eyeFilterOverlay.setBackgroundColor(android.graphics.Color.parseColor("#4D000000")); // Black 30%
                }
                
                // Save setting locally if you want
                android.preference.PreferenceManager.getDefaultSharedPreferences(this).edit().putInt("eye_filter_mode", which).apply();
                dialog.dismiss();
            })
            .setPositiveButton("إبقاء الشاشة مضاءة", (dialog, which) -> {
                getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                com.fire.mangareader.util.SystemUtils.safeToast(this, "تم تفعيل إبقاء الشاشة مضاءة");
            })
            .setNegativeButton("إلغاء", null)
            .show();
    }

    
    private void loadChapterListFromDb() {
        new Thread(() -> {
            try {
                List<com.fire.mangareader.data.database.CachedChapter> cachedChapters = AppDatabase.getInstance(this).cacheDao().getMangaChapters(mangaUrl);
                if (cachedChapters != null && !cachedChapters.isEmpty()) {
                    for (int i = 0; i < cachedChapters.size(); i++) {
                        if (cachedChapters.get(i).chapterUrl.equals(chapterUrl)) {
                            
                            if (i - 1 >= 0) {
                                nextChapterUrl = cachedChapters.get(i - 1).chapterUrl;
                                nextChapterTitle = cachedChapters.get(i - 1).title;
                            }
                            if (i + 1 < cachedChapters.size()) {
                                prevChapterUrl = cachedChapters.get(i + 1).chapterUrl;
                                prevChapterTitle = cachedChapters.get(i + 1).title;
                            }
                            break;
                        }
                    }
                    
                    runOnUiThread(() -> {
                        btnNextChapter.setAlpha(nextChapterUrl != null ? 1.0f : 0.3f);
                        btnPreviousChapter.setAlpha(prevChapterUrl != null ? 1.0f : 0.3f);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void openNewChapter(String url, String title) {
        Intent intent = new Intent(ChapterReaderActivity.this, ChapterReaderActivity.class);
        intent.putExtra("chapterUrl", url);
        intent.putExtra("mangaUrl", mangaUrl);
        intent.putExtra("chapterTitle", title);
        startActivity(intent);
        finish(); 
    }

    private void setupSeekBarListener() {
        pageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    tvCurrentPageSeek.setText(String.valueOf(progress + 1));
                    layoutManager.scrollToPositionWithOffset(progress, 0); 
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void checkIfChapterIsDownloadedAndLoad() {
        if (chapterUrl == null) {
            com.fire.mangareader.util.SystemUtils.safeToast(this, "رابط الفصل غير صالح");
            return;
        }
        
        loadingProgressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                java.io.File mangaFolder = new java.io.File(getFilesDir(), String.valueOf(mangaUrl.hashCode()));
                java.io.File chapterFolder = new java.io.File(mangaFolder, String.valueOf(chapterUrl.hashCode()));

                if (chapterFolder.exists() && chapterFolder.isDirectory()) {
                    java.io.File[] files = chapterFolder.listFiles();
                    if (files != null && files.length > 0) {
                        
                        java.util.Arrays.sort(files, (f1, f2) -> {
                            try {
                                int n1 = Integer.parseInt(f1.getName().replace(".jpg", ""));
                                int n2 = Integer.parseInt(f2.getName().replace(".jpg", ""));
                                return Integer.compare(n1, n2);
                            } catch (Exception e) {
                                return f1.getName().compareTo(f2.getName());
                            }
                        });

                        List<String> localPages = new ArrayList<>();
                        for (java.io.File f : files) {
                            localPages.add(f.getAbsolutePath()); 
                        }

                        runOnUiThread(() -> {
                            loadingProgressBar.setVisibility(View.GONE);
                            scraperWebView.setVisibility(View.GONE); 
                            
                            List<com.fire.mangareader.domain.model.reader.Page> pageList = new java.util.ArrayList<>();
                            for (int i = 0; i < localPages.size(); i++) {
                                pageList.add(new com.fire.mangareader.domain.model.reader.Page(i, localPages.get(i), localPages.get(i), null));
                            }
                            adapter.setPages(pageList, null, null); 
                            
                            int total = localPages.size();
                            tvPageIndicator.setText("1 / " + total);
                            pageSeekBar.setMax(total - 1);
                            tvTotalPagesSeek.setText(String.valueOf(total));
                            
                            Toast.makeText(ChapterReaderActivity.this, "وضع القراءة بدون إنترنت ✈️", Toast.LENGTH_SHORT).show();
                        });
                        return; 
                    }
                }

                runOnUiThread(() -> {
                    setupWebViewScraper();
                    scraperWebView.loadUrl(chapterUrl);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    setupWebViewScraper();
                    scraperWebView.loadUrl(chapterUrl);
                });
            }
        }).start();
    }

    private void setupWebViewScraper() {
        WebSettings settings = scraperWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        settings.setLoadsImagesAutomatically(true); settings.setBlockNetworkImage(false);
        settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);

        String agent = android.webkit.WebSettings.getDefaultUserAgent(this);
        settings.setUserAgentString(agent);
        com.fire.mangareader.data.network.MangaScraper.globalUserAgent = agent;

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(scraperWebView, true);

        scraperWebView.setWebChromeClient(new WebChromeClient());
        scraperWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean onRenderProcessGone(android.webkit.WebView view, android.webkit.RenderProcessGoneDetail detail) {
                if (view != null) {
                    view.destroy();
                }
                return true;
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (request.isForMainFrame()) {
                    int statusCode = errorResponse.getStatusCode();
                    runOnUiThread(() -> {
                        loadingProgressBar.setVisibility(View.GONE);
                        if (statusCode == 404) {
                            Toast.makeText(ChapterReaderActivity.this, "عذراً، هذا الفصل غير موجود أو تم حذفه (404)", Toast.LENGTH_LONG).show();
                        } else if (statusCode == 504) {
                            Toast.makeText(ChapterReaderActivity.this, "سيرفر الموقع عليه ضغط شديد (504)، حاول لاحقاً", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                
                if (url != null && url.contains("dilar.tube")) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        view.evaluateJavascript(
                            "(function() { " +
                            "  try { " +
                            "    if (window.__PRELOADED_STATE__) { " +
                            "       var data = window.__PRELOADED_STATE__.readerDataAction.readerData; " +
                            "       var result = { " +
                            "          storage_key: data.release.storage_key, " +
                            "          pages: data.release.pages " +
                            "       }; " +
                            "       return JSON.stringify(result); " +
                            "    } " +
                            "  } catch(e) {} " +
                            "  return null; " +
                            "})();",
                            jsonResult -> {
                                if (jsonResult != null && !jsonResult.equals("null") && !jsonResult.equals("\"null\"")) {
                                    try {
                                        String unescaped = jsonResult.replaceAll("^\"|\"$", "").replace("\\\"", "\"").replace("\\\\", "\\");
                                        org.json.JSONObject obj = new org.json.JSONObject(unescaped);
                                        String storageKey = obj.getString("storage_key");
                                        org.json.JSONArray pagesArray = obj.getJSONArray("pages");
                                        
                                        java.util.List<String> pagesList = new java.util.ArrayList<>();
                                        for (int i = 0; i < pagesArray.length(); i++) {
                                            String pageFileName = pagesArray.getString(i);
                                            String fullImgUrl = "https://dilar.tube/cdn-cgi/image/format=webp,width=700/" + storageKey + "/" + pageFileName;
                                            pagesList.add(fullImgUrl);
                                        }
                                        
                                        runOnUiThread(() -> {
                                            loadingProgressBar.setVisibility(View.GONE);
                                            scraperWebView.setVisibility(View.GONE);
                                            
                                            java.util.List<com.fire.mangareader.domain.model.reader.Page> pageList = new java.util.ArrayList<>();
                                            for (int i = 0; i < pagesList.size(); i++) {
                                                pageList.add(new com.fire.mangareader.domain.model.reader.Page(i, pagesList.get(i), pagesList.get(i), null));
                                            }
                                            adapter.setPages(pageList, null, url);
                                            
                                            int total = pagesList.size();
                                            tvPageIndicator.setText("1 / " + total);
                                            pageSeekBar.setMax(total - 1);
                                            tvTotalPagesSeek.setText(String.valueOf(total));
                                        });
                                        return; 
                                    } catch (Exception ignored) {}
                                }
                                checkAndParseStandardHtml(view, url);
                            }
                        );
                    }, 2500);
                    return;
                }
                
                checkAndParseStandardHtml(view, url);
            }

            private void checkAndParseStandardHtml(WebView view, String url) {
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { view.evaluateJavascript(

                    "(function() { return document.documentElement.outerHTML; })();",
                    html -> {
                        if (html == null || html.equals("null")) return;

                        if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare") || html.contains("you have been blocked") || html.contains("cf-error-details")) {
                            runOnUiThread(() -> {
                                loadingProgressBar.setVisibility(View.GONE);
                                scraperWebView.setVisibility(View.VISIBLE); scraperWebView.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(1, 1)); scraperWebView.setAlpha(0.01f);
                                Toast.makeText(ChapterReaderActivity.this, "يرجى حل اختبار الحماية لفتح الفصل", Toast.LENGTH_LONG).show();
                            });
                            return; 
                        }

                        String cookies = CookieManager.getInstance().getCookie(url);
                        String cleanHtml = html.replaceAll("^\"|\"$", "").replace("\\u003C", "<").replace("\\u003E", ">").replace("\\\"", "\"");
                        parseHtmlAndExtractImages(cleanHtml, cookies, url);
                    }
                ); }, 2500);
            }
        });
    }

    private void parseHtmlAndExtractImages(String html, String cookies, String refererUrl) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.parse(html);
                List<String> pages = new ArrayList<>();
                Set<String> uniqueUrls = new HashSet<>();

                String[] selectors = {
                    "div.reading-content img", "div.page-break img", ".wp-manga-chapter-img",
                    "div.single-chapter img", ".reader-area img", "#readerarea img",
                    ".read-container img", ".chapter-content img", ".reading-content-wrap img",
                    "div.entry-content img", "div.entry-content p img", "div.text-center img",
                    "div.text-left img", "div[id*='chapter'] img", "div[class*='chapter'] img",
                    "#vungdoc img", ".vung-doc img", ".chapter-video-frame img", ".main-col img"
                };

                for (String sel : selectors) {
                    Elements images = doc.select(sel);
                    for (Element img : images) {
                        String imgUrl = com.fire.mangareader.data.network.MangaScraper.extractImageUrlFromImgTag(img);
                        if (com.fire.mangareader.data.network.MangaScraper.isChapterPageImage(imgUrl, img) && !uniqueUrls.contains(imgUrl)) {
                            pages.add(com.fire.mangareader.data.network.MangaScraper.getHighResImageUrl(imgUrl));
                            uniqueUrls.add(imgUrl);
                        }
                    }
                    if (pages.size() >= 3) break;
                }

                if (pages.isEmpty()) {
                    Elements allImages = doc.select("img");
                    for (Element img : allImages) {
                        String imgUrl = com.fire.mangareader.data.network.MangaScraper.extractImageUrlFromImgTag(img);
                        if (com.fire.mangareader.data.network.MangaScraper.isChapterPageImage(imgUrl, img) && !uniqueUrls.contains(imgUrl)) {
                            pages.add(com.fire.mangareader.data.network.MangaScraper.getHighResImageUrl(imgUrl));
                            uniqueUrls.add(imgUrl);
                        }
                    }
                }

                if (pages.isEmpty()) {
                    Elements scripts = doc.select("script");
                    java.util.regex.Pattern regex = java.util.regex.Pattern.compile("https?://[^\\s\"'<>]+\\.(?:jpg|jpeg|png|webp|gif|avif)(?:\\?[^\\s\"'<>]*)?", java.util.regex.Pattern.CASE_INSENSITIVE);
                    for (Element script : scripts) {
                        String scriptHtml = script.html().replace("\\/", "/");
                        java.util.regex.Matcher matcher = regex.matcher(scriptHtml);
                        while (matcher.find()) {
                            String foundUrl = matcher.group();
                            if (com.fire.mangareader.data.network.MangaScraper.isChapterPageImage(foundUrl, null) && !uniqueUrls.contains(foundUrl)) {
                                pages.add(com.fire.mangareader.data.network.MangaScraper.getHighResImageUrl(foundUrl));
                                uniqueUrls.add(foundUrl);
                            }
                        }
                    }
                }

                runOnUiThread(() -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    if (!pages.isEmpty()) {
                        scraperWebView.setVisibility(View.GONE); 
                        List<com.fire.mangareader.domain.model.reader.Page> pageList = new java.util.ArrayList<>();
                        for (int i = 0; i < pages.size(); i++) {
                            pageList.add(new com.fire.mangareader.domain.model.reader.Page(i, pages.get(i), pages.get(i), null));
                        }
                        adapter.setPages(pageList, cookies, refererUrl);
                        
                        int total = pages.size();
                        tvPageIndicator.setText("1 / " + total);
                        pageSeekBar.setMax(total - 1);
                        tvTotalPagesSeek.setText(String.valueOf(total));
                        
                        // 🚀 التحميل المسبق لصفحات الفصل التالي في الخلفية
                        prefetchNextChapter(cookies);
                    } else {
                        Toast.makeText(ChapterReaderActivity.this, "لم يتم العثور على صور في هذا الفصل.", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(ChapterReaderActivity.this, "حدث خطأ أثناء معالجة الصور", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void prefetchNextChapter(String cookies) {
        if (nextChapterUrl == null || nextChapterUrl.isEmpty()) return;
        new Thread(() -> {
            try {
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(nextChapterUrl)
                        .header("Cookie", cookies != null ? cookies : com.fire.mangareader.data.network.MangaScraper.globalCookies)
                        .header("Referer", chapterUrl != null ? chapterUrl : nextChapterUrl)
                        .header("User-Agent", com.fire.mangareader.data.network.MangaScraper.globalUserAgent)
                        .build();
                        
                okhttp3.Response response = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(request).execute();
                if (!response.isSuccessful()) return;
                
                String html = response.body().string();
                
                com.fire.mangareader.data.parser.DynamicParserEngine engine = new com.fire.mangareader.data.parser.DynamicParserEngine();
                com.fire.mangareader.domain.model.parser.ExtractorConfig config = com.fire.mangareader.data.parser.ParserConfigManager.INSTANCE.getExtractor("PagesPrefetch");
                
                if (config == null) {
                    java.util.Map<String, com.fire.mangareader.domain.model.parser.SelectorConfig> selectors = new java.util.HashMap<>();
                    selectors.put("url", new com.fire.mangareader.domain.model.parser.SelectorConfig("", "data-src", null));
                    
                    java.util.List<com.fire.mangareader.domain.model.parser.UrlTransformation> transforms = new java.util.ArrayList<>();
                    transforms.add(new com.fire.mangareader.domain.model.parser.UrlTransformation(
                            com.fire.mangareader.domain.model.parser.TransformationType.SUBDOMAIN_REPLACE, 
                            "^//", "https://"));
                            
                    config = new com.fire.mangareader.domain.model.parser.ExtractorConfig(
                            "PagesPrefetch",
                            com.fire.mangareader.domain.model.parser.ExtractorType.PAGES,
                            ".reading-content img, .page-break img, #vungdoc img, .vung-doc img, .chapter-video-frame img",
                            new java.util.HashMap<>(),
                            new java.util.ArrayList<>(),
                            selectors,
                            com.fire.mangareader.domain.model.parser.ResponseFormat.HTML,
                            transforms
                    );
                }
                
                java.util.List<String> parsedPages = engine.parsePages(html, config);
                if (parsedPages.isEmpty()) {
                     config = config.copy(
                             config.getName(), config.getType(), "img", config.getFields(), config.getParameters(),
                             java.util.Collections.singletonMap("url", new com.fire.mangareader.domain.model.parser.SelectorConfig("", "src", null)),
                             config.getResponse_format(), config.getUrl_transformations()
                     );
                     parsedPages = engine.parsePages(html, config);
                }

                int count = 0;
                for (String imgUrl : parsedPages) {
                    if (count >= 4) break;
                    if (!imgUrl.isEmpty() && !imgUrl.endsWith(".gif")) {
                        java.io.File tempCacheFile = new java.io.File(getCacheDir(), "img_" + Math.abs(imgUrl.hashCode()) + ".jpg");
                        if (!tempCacheFile.exists()) {
                            okhttp3.Request req = new okhttp3.Request.Builder()
                                    .url(imgUrl)
                                    .header("Referer", nextChapterUrl)
                                    .build();
                            okhttp3.Response resp = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(req).execute();
                            if (resp.isSuccessful() && resp.body() != null) {
                                java.io.FileOutputStream fos = new java.io.FileOutputStream(tempCacheFile);
                                fos.write(resp.body().bytes());
                                fos.flush();
                                fos.close();
                            }
                        }
                        count++;
                    }
                }
            } catch (Exception ignored) {}
        }).start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isUiVisible) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void toggleUi() {
        isUiVisible = !isUiVisible;
        if (isUiVisible) {
            uiOverlay.setVisibility(View.VISIBLE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else {
            uiOverlay.setVisibility(View.GONE);
            hideSystemUI();
        }
    }

    private void saveReadingProgress(int currentPage, int totalPages) {
        new Thread(() -> {
            try {
                com.fire.mangareader.data.database.ChapterState state = AppDatabase.getInstance(this).chapterStateDao().getChapterState(chapterUrl);
                if (state == null) {
                    state = new com.fire.mangareader.data.database.ChapterState();
                    state.chapterUrl = chapterUrl;
                    state.mangaUrl = mangaUrl;
                }
                state.lastPage = currentPage - 1;
                state.isRead = true;
                if (currentPage == totalPages && totalPages > 0) {
                    state.isCompleted = true;
                }
                AppDatabase.getInstance(this).chapterStateDao().insert(state);
                
                // Add to Supabase Read History
                com.fire.mangareader.data.network.SupabaseManager.getInstance(this).markChapterRead(mangaUrl, chapterUrl, chapterTitle, null);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    @Override
    protected void onDestroy() {
        if (scraperWebView != null) {
            scraperWebView.destroy();
            scraperWebView = null;
        }
        
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }

        super.onDestroy();
    }
    @Override
    public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (isHorizontalMode) {
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                if (firstVisible + 1 < adapter.getItemCount()) {
                    recyclerView.smoothScrollToPosition(firstVisible + 1);
                }
            } else {
                recyclerView.smoothScrollBy(0, (int) (recyclerView.getHeight() * 0.75f));
            }
            return true;
        } else if (keyCode == android.view.KeyEvent.KEYCODE_VOLUME_UP) {
            if (isHorizontalMode) {
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                if (firstVisible - 1 >= 0) {
                    recyclerView.smoothScrollToPosition(firstVisible - 1);
                }
            } else {
                recyclerView.smoothScrollBy(0, -(int) (recyclerView.getHeight() * 0.75f));
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
