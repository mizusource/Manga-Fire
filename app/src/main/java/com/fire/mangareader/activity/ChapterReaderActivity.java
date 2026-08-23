package com.fire.mangareader.activity;

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
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.reader.viewer.ReaderColorFilterView;
import com.fire.mangareader.reader.viewer.WebtoonAdapter;
import com.fire.mangareader.reader.viewer.WebtoonRecyclerView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class ChapterReaderActivity extends AppCompatActivity {

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

    private boolean isUiVisible = false;
    private boolean isFilterActive = false;
    
    private String chapterUrl;
    private String mangaUrl;
    
    private String nextChapterUrl = null, nextChapterTitle = null;
    private String prevChapterUrl = null, prevChapterTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= 28) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        
        setContentView(R.layout.activity_chapter_reader);

        chapterUrl = getIntent().getStringExtra("chapterUrl");
        mangaUrl = getIntent().getStringExtra("mangaUrl");
        String chapterTitle = getIntent().getStringExtra("chapterTitle");

        recyclerView = findViewById(R.id.recyclerView);
        colorFilterView = findViewById(R.id.colorFilterView);
        uiOverlay = findViewById(R.id.uiOverlay);
        tvPageIndicator = findViewById(R.id.tvPageIndicator);
        tvChapterTitle = findViewById(R.id.tvChapterTitle);
        btnBack = findViewById(R.id.btnBack);
        btnEyeFilter = findViewById(R.id.btnEyeFilter);
        scraperWebView = findViewById(R.id.scraperWebView);

        pageSeekBar = findViewById(R.id.pageSeekBar);
        tvCurrentPageSeek = findViewById(R.id.tvCurrentPageSeek);
        tvTotalPagesSeek = findViewById(R.id.tvTotalPagesSeek);
        btnNextChapter = findViewById(R.id.btnNextChapter);
        btnPreviousChapter = findViewById(R.id.btnPreviousChapter);

        if (chapterTitle != null) tvChapterTitle.setText(chapterTitle);

        loadingProgressBar = new ProgressBar(this);
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
        recyclerView.setHasFixedSize(true); // تسريع الرسم

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
    
    private void loadChapterListFromDb() {
        new Thread(() -> {
            try {
                List<com.fire.mangareader.database.CachedChapter> cachedChapters = AppDatabase.getInstance(this).cacheDao().getMangaChapters(mangaUrl);
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
            Toast.makeText(this, "رابط الفصل غير صالح", Toast.LENGTH_SHORT).show();
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
                            
                            adapter.setPages(localPages, null, null); 
                            
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

        settings.setLoadsImagesAutomatically(true);
        settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setMediaPlaybackRequiresUserGesture(false);

        String agent = "Mozilla/5.0 (Linux; Android 14; SM-A366B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36";
        settings.setUserAgentString(agent);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(scraperWebView, true);

        scraperWebView.setWebChromeClient(new WebChromeClient());
        scraperWebView.setWebViewClient(new WebViewClient() {
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
                view.evaluateJavascript(
                    "(function() { return document.documentElement.outerHTML; })();",
                    html -> {
                        if (html == null || html.equals("null")) return;

                        if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare")) {
                            runOnUiThread(() -> {
                                loadingProgressBar.setVisibility(View.GONE);
                                scraperWebView.setVisibility(View.VISIBLE);
                                Toast.makeText(ChapterReaderActivity.this, "يرجى حل اختبار الحماية لفتح الفصل", Toast.LENGTH_LONG).show();
                            });
                            return; 
                        }

                        String cookies = CookieManager.getInstance().getCookie(url);
                        String cleanHtml = html.replaceAll("^\"|\"$", "").replace("\\u003C", "<").replace("\\\"", "\"");
                        parseHtmlAndExtractImages(cleanHtml, cookies, url);
                    }
                );
            }
        });
    }

    private void parseHtmlAndExtractImages(String html, String cookies, String refererUrl) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.parse(html);
                List<String> pages = new ArrayList<>();
                Elements images = doc.select(".reading-content img, .page-break img, #vungdoc img, .vung-doc img, .chapter-video-frame img");
                
                for (Element img : images) {
                    String imgUrl = img.attr("data-src");
                    if (imgUrl.isEmpty()) imgUrl = img.attr("src");
                    if (!imgUrl.isEmpty() && !imgUrl.endsWith(".gif")) {
                        pages.add(imgUrl.trim());
                    }
                }

                runOnUiThread(() -> {
                    loadingProgressBar.setVisibility(View.GONE);
                    if (!pages.isEmpty()) {
                        scraperWebView.setVisibility(View.GONE); 
                        adapter.setPages(pages, cookies, refererUrl);
                        
                        int total = pages.size();
                        tvPageIndicator.setText("1 / " + total);
                        pageSeekBar.setMax(total - 1);
                        tvTotalPagesSeek.setText(String.valueOf(total));
                        
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
                com.fire.mangareader.database.ChapterState state = AppDatabase.getInstance(this).chapterStateDao().getChapterState(chapterUrl);
                if (state == null) {
                    state = new com.fire.mangareader.database.ChapterState();
                    state.chapterUrl = chapterUrl;
                    state.mangaUrl = mangaUrl;
                }
                state.lastPage = currentPage - 1;
                state.isRead = true;
                if (currentPage == totalPages && totalPages > 0) {
                    state.isCompleted = true;
                }
                AppDatabase.getInstance(this).chapterStateDao().insert(state);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
