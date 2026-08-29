package com.fire.mangareader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.ChapterAdapter;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.database.LibraryItem;
import com.fire.mangareader.model.Chapter;
import com.fire.mangareader.network.MangaScraper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class MangaDetailActivity extends AppCompatActivity {
    private ImageView coverImage, coverImageBlur;
    private TextView titleText, statusText, descriptionText;
    private RecyclerView chaptersRecycler;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout; 
    private ImageView btnFavorite, btnComments;
    private android.widget.LinearLayout btnFavoriteContainer, btnCommentsContainer;
    private TextView tvFavoriteText;
    private Chapter nextChapterToRead = null;
    private String mangaUrl, mangaTitle, mangaCover;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList;
    private boolean isFavorite = false;
    private String currentLibraryStatus = "";

    
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_detail);

        mangaUrl = getIntent().getStringExtra("mangaUrl");
        mangaTitle = getIntent().getStringExtra("mangaTitle");
        mangaCover = getIntent().getStringExtra("mangaCover");

        coverImage = findViewById(R.id.mangaCover);
        if (mangaUrl != null && coverImage != null) {
            androidx.core.view.ViewCompat.setTransitionName(coverImage, "cover_transition_" + mangaUrl);
            supportPostponeEnterTransition();
        }
        coverImageBlur = findViewById(R.id.mangaCoverBlur);
        // titleText = findViewById(R.id.toolbarTitle);
        TextView mangaTitleDetail = findViewById(R.id.mangaTitleDetail);
        TextView authorText = findViewById(R.id.mangaAuthor);
        if (mangaTitleDetail != null) mangaTitleDetail.setText(mangaTitle);
        statusText = findViewById(R.id.mangaStatus);
        descriptionText = findViewById(R.id.mangaDescription);
        chaptersRecycler = findViewById(R.id.chaptersRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); 
        btnFavorite = findViewById(R.id.btnFavorite);
        // btnFavoriteContainer = findViewById(R.id.btnFavoriteContainer);
        // btnCommentsContainer
        // tvFavoriteText
        btnComments = findViewById(R.id.btnComments);

        // titleText.setText(mangaTitle);
        
        android.widget.Button btnStartReading = findViewById(R.id.btnStartReading);
        if (btnStartReading != null) {
            btnStartReading.setOnClickListener(v -> {
                if (chapterList != null && !chapterList.isEmpty()) {
                    // Try to find the first unread chapter (or just start from chapter 1)
                    com.fire.mangareader.model.Chapter firstChapter = chapterList.get(chapterList.size() - 1);
                    android.content.Intent intent = new android.content.Intent(MangaDetailActivity.this, ChapterReaderActivity.class);
                    intent.putExtra("chapterUrl", firstChapter.getUrl());
                    intent.putExtra("mangaUrl", mangaUrl);
                    intent.putExtra("mangaTitle", mangaTitle);
                    intent.putExtra("mangaCover", mangaCover);
                    intent.putExtra("chapterTitle", firstChapter.getTitle());
                    startActivity(intent);
                } else {
                    android.widget.Toast.makeText(this, "لا توجد فصول متاحة بعد", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        View detailsContainer = findViewById(R.id.detailsContainer);
         

        if (tabLayout != null) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        detailsContainer.setVisibility(View.VISIBLE);
                        chaptersRecycler.setVisibility(View.GONE);
                    } else {
                        detailsContainer.setVisibility(View.GONE);
                        chaptersRecycler.setVisibility(View.VISIBLE);
                    }
                }
                @Override public void onTabUnselected(TabLayout.Tab tab) {}
                @Override public void onTabReselected(TabLayout.Tab tab) {}
            });
        }
        
        Glide.with(this).load(mangaCover).override(15, 15).into(coverImageBlur);
        
        com.bumptech.glide.Glide.with(this).asBitmap().load(mangaCover).into(new com.bumptech.glide.request.target.CustomTarget<android.graphics.Bitmap>() {
            @Override
            public void onResourceReady(android.graphics.Bitmap resource, com.bumptech.glide.request.transition.Transition<? super android.graphics.Bitmap> transition) {
                coverImage.setImageBitmap(resource);
                supportStartPostponedEnterTransition();
                androidx.palette.graphics.Palette.from(resource).generate(palette -> {
                    if (palette != null) {
                        int defaultColor = android.graphics.Color.parseColor("#121212");
                        int vibrantColor = palette.getVibrantColor(defaultColor);
                        int darkVibrantColor = palette.getDarkVibrantColor(defaultColor);
                        
                        

                        ImageView btnFavorite = findViewById(R.id.btnFavorite);
        // btnFavoriteContainer = findViewById(R.id.btnFavoriteContainer);
        // btnCommentsContainer
        // tvFavoriteText
                    }
                });
            }
            @Override
            public void onLoadFailed(@androidx.annotation.Nullable android.graphics.drawable.Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                supportStartPostponedEnterTransition();
            }
            @Override
            public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
        });

        chapterList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(this, chapterList, mangaUrl, mangaTitle, mangaCover);
        chaptersRecycler.setLayoutManager(new LinearLayoutManager(this));
        chaptersRecycler.setAdapter(chapterAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadMangaDetailsViaWebView(true);
        });

        loadReadChapters();
        checkFavoriteStatus();
        checkRoomCacheAndLoad();
        loadAniListMetadata();
        setupRatingButtons();


        ImageView btnDownloadMultiple = findViewById(R.id.btnDownloadMultiple);
        if (btnDownloadMultiple != null) {
            btnDownloadMultiple.setOnClickListener(v -> {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(this, btnDownloadMultiple);
                popup.getMenu().add(0, 1, 0, "تنزيل جميع الفصول");
                popup.getMenu().add(0, 2, 0, "تنزيل الفصول غير المقروءة");
                popup.setOnMenuItemClickListener(item -> {
                    if (chapterList == null || chapterList.isEmpty()) {
                        Toast.makeText(this, "لا توجد فصول", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    int count = 0;
                    for (Chapter c : chapterList) {
                        if (item.getItemId() == 2) {
                            // TODO: check if read
                            // For simplicity, download all in this demo or implement full check
                        }
                        com.fire.mangareader.service.DownloadService.startDownload(this, mangaUrl, c.getUrl(), c.getTitle());
                        count++;
                        if (item.getItemId() == 2 && count >= 10) break; // limit to 10 for "unread" to prevent overload
                    }
                    Toast.makeText(this, "بدء تنزيل " + count + " فصول في الخلفية", Toast.LENGTH_SHORT).show();
                    return true;
                });
                popup.show();
            });
        }

        if (btnFavoriteContainer != null) {
            btnFavoriteContainer.setOnClickListener(v -> toggleFavorite());
        } else {
            btnFavorite.setOnClickListener(v -> toggleFavorite());
        }

                View btnUserRating = findViewById(R.id.btnUserRating);
        if (btnUserRating != null) {
            btnUserRating.setOnClickListener(v -> {
                String[] ratings = {"10/10 - أسطورية", "9/10 - ممتازة", "8/10 - جيدة جداً", "7/10 - جيدة", "6/10 - مقبولة", "5/10 - متوسطة", "إزالة التقييم"};
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("تقييمك للمانجا")
                        .setItems(ratings, (dialog, which) -> {
                            // TextView tvUserRating = findViewById(R.id.tvUserRating);
                            ImageView ivUserRatingStar = findViewById(R.id.ivUserRatingStar);
                            if (which == 6) {
                                // tvUserRating
                                ivUserRatingStar.setImageResource(R.drawable.ic_star_outline);
                                ivUserRatingStar.setColorFilter(null); // Clear filter
                            } else {
                                // tvUserRating
                                ivUserRatingStar.setImageResource(R.drawable.ic_star);
                                ivUserRatingStar.setColorFilter(android.graphics.Color.parseColor("#FF9800"));
                            }
                            android.widget.Toast.makeText(this, "تم حفظ تقييمك", android.widget.Toast.LENGTH_SHORT).show();
                        })
                        .show();
            });
        }
        // View btnMyList
        if (false) {
            // btnMyList
        }
        if (btnComments != null) { btnComments.setOnClickListener(v -> {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    CommentsBottomSheetDialog bottomSheet = CommentsBottomSheetDialog.newInstance(mangaUrl);
                    bottomSheet.show(getSupportFragmentManager(), "CommentsBottomSheet");
                }).start();
            });
        } else {
            btnComments.setOnClickListener(v -> {
                CommentsBottomSheetDialog bottomSheet = CommentsBottomSheetDialog.newInstance(mangaUrl);
                bottomSheet.show(getSupportFragmentManager(), "CommentsBottomSheet");
            });
        }

    }

    private void checkRoomCacheAndLoad() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            com.fire.mangareader.database.CachedManga cachedManga = AppDatabase.getInstance(this).cacheDao().getMangaDetails(mangaUrl);
            List<com.fire.mangareader.database.CachedChapter> cachedChapters = AppDatabase.getInstance(this).cacheDao().getMangaChapters(mangaUrl);

            runOnUiThread(() -> {
                if (cachedManga != null && cachedChapters != null && !cachedChapters.isEmpty()) {
                    descriptionText.setText(cachedManga.description);
                    statusText.setText("الحالة: " + cachedManga.status);
                    chapterList.clear();
                    for (com.fire.mangareader.database.CachedChapter cc : cachedChapters) {
                        Chapter c = new Chapter();
                        c.setUrl(cc.chapterUrl);
                        c.setTitle(cc.title);
                        chapterList.add(c);
                    }
                    chapterAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    loadReadChapters();
                } else {
                    loadMangaDetailsViaWebView(false);
                }
            });
        }).start();
    }

    private void saveToRoomCache(String desc, String status, List<Chapter> chapters) {
        new Thread(() -> {
            com.fire.mangareader.database.CachedManga cm = new com.fire.mangareader.database.CachedManga();
            cm.mangaUrl = mangaUrl;
            cm.description = desc;
            cm.status = status;

            List<com.fire.mangareader.database.CachedChapter> ccList = new ArrayList<>();
            for (int i = 0; i < chapters.size(); i++) {
                Chapter c = chapters.get(i);
                com.fire.mangareader.database.CachedChapter cc = new com.fire.mangareader.database.CachedChapter();
                cc.chapterUrl = c.getUrl();
                cc.mangaUrl = mangaUrl;
                cc.title = c.getTitle();
                cc.chapterOrder = i;
                ccList.add(cc);
            }
            AppDatabase.getInstance(MangaDetailActivity.this).cacheDao().cacheMangaAndChapters(cm, ccList);
        }).start();
    }


    private void loadMangaDetailsViaWebView(boolean isSilentBackgroundFetch) {
        if (!isSilentBackgroundFetch) progressBar.setVisibility(View.VISIBLE);

        android.view.ViewGroup rootView = findViewById(android.R.id.content);
        android.webkit.WebView webView = new android.webkit.WebView(this);
        
        webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(1, 1));
        webView.setAlpha(0.0f); 
        rootView.addView(webView);

        android.webkit.WebSettings settings = webView.getSettings();
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
        settings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        settings.setMediaPlaybackRequiresUserGesture(false); 

        String agent = android.webkit.WebSettings.getDefaultUserAgent(this);
        settings.setUserAgentString(agent);
        MangaScraper.globalUserAgent = agent; 

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebChromeClient(new android.webkit.WebChromeClient());
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            
            
            public void onReceivedError(android.webkit.WebView view, android.webkit.WebResourceRequest request, android.webkit.WebResourceError error) { if(!request.isForMainFrame()) return; runOnUiThread(() -> { progressBar.setVisibility(View.GONE); swipeRefreshLayout.setRefreshing(false); Toast.makeText(MangaDetailActivity.this, "Network Error: " + error.getDescription(), Toast.LENGTH_SHORT).show(); });
                rootView.removeView(webView);
                try { webView.stopLoading(); webView.loadUrl("about:blank"); new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { try { webView.destroy(); } catch (Exception ignored2) {} }, 1500); } catch (Exception ignored) {}
            }
            boolean[] isProcessed = {false};
            public void onPageFinished(android.webkit.WebView view, String url) {
                if (isProcessed[0]) return;
                String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                if (cookies != null) { com.fire.mangareader.network.MangaScraper.globalCookies = cookies; getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().putString("cloudflare_cookies", cookies).apply(); }
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { 
                    try {
                        view.evaluateJavascript("(function() { try { if (document.getElementById(\"manga-chapters-holder\") && document.getElementById(\"manga-chapters-holder\").innerHTML.trim() === \"\") { var req = new XMLHttpRequest(); req.open(\"POST\", window.location.href + (window.location.href.endsWith(\"/\") ? \"\" : \"/\") + \"ajax/chapters/\", false); req.send(); if (req.status === 200) { document.getElementById(\"manga-chapters-holder\").innerHTML = req.responseText; } } } catch(e){} return document.documentElement.outerHTML; })();", html -> {
                            if (isProcessed[0]) return;
                            if (html == null || html.equals("null")) {
                                if (!isSilentBackgroundFetch) {
                                    runOnUiThread(() -> {
                                        Toast.makeText(MangaDetailActivity.this, "فشل جلب محتوى الصفحة", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        swipeRefreshLayout.setRefreshing(false);
                                    });
                                }
                                return;
                            }

                            if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare") || html.contains("you have been blocked") || html.contains("cf-error-details")) { 
                                runOnUiThread(() -> { 
                                    progressBar.setVisibility(View.GONE); 
                                    webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(1, 1)); 
                                    webView.setAlpha(0.01f); 
                                    Toast.makeText(MangaDetailActivity.this, "يرجى الانتظار لتخطي حماية Cloudflare...", Toast.LENGTH_LONG).show(); 
                                });
                                return; 
                            }
                            
                            isProcessed[0] = true;
                            rootView.removeView(webView); 
                            try { 
                                webView.stopLoading(); 
                                webView.loadUrl("about:blank"); 
                                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { 
                                    try { webView.destroy(); } catch (Exception ignored2) {} 
                                }, 1500); 
                            } catch (Exception ignored) {} 
                            
                            String cleanHtml = html.replaceAll("^\"|\"$", "").replace("\\u003C", "<").replace("\\u003E", ">").replace("\\\"", "\"").replace("\\n", " ").replace("\\t", " ").replace("\\r", " ").replace("\\\\", "");
                            
                            parseHtmlLocally(cleanHtml, isSilentBackgroundFetch);
                        }); 
                    } catch (Exception ignored) {}
                }, 2500);
            }
        });
        
        webView.loadUrl(mangaUrl);
    }

    private void parseHtmlLocally(String html, boolean isSilentBackgroundFetch) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.parse(html, mangaUrl);
                Element descElement = doc.select(".summary__content p, .manga-excerpt p, .description-summary p, .summary_content p, .post-content_item p, .entry-content p, #sinopsis p, .desc p").first();
                String tempDescription = "لا يوجد وصف متاح.";
                if (descElement != null && !descElement.text().trim().isEmpty()) {
                    tempDescription = descElement.text().trim();
                } else {
                    Element fallbackDesc = doc.select(".summary__content, .manga-excerpt, .description-summary, .summary_content, .entry-content, #sinopsis, .desc").first();
                    if (fallbackDesc != null) tempDescription = fallbackDesc.ownText().trim();
                }

                Element statusElement = doc.select(".post-status .summary-content, .post-status, .info-status, .imptdt").first();
                String tempStatus = "مستمرة";
                Element authorElement = doc.select(".author-content, .author-info, .manga-author, .post-content_item:contains(Author) .summary-content, .post-content_item:contains(المؤلف) .summary-content").first();
                String tempAuthor = "غير معروف";
                if (authorElement != null && !authorElement.text().trim().isEmpty()) {
                    tempAuthor = authorElement.text().trim();
                }
                if (statusElement != null) {
                    String statusText = statusElement.text();
                    if (statusText.contains("مكتمل") || statusText.contains("Completed") || statusText.contains("End") || statusText.contains("انتهى")) {
                        tempStatus = "مكتملة";
                    }
                }

                List<Chapter> chapters = new ArrayList<>();
                Elements chapterElements = doc.select("li.wp-manga-chapter, .listing-chapters_wrap li, ul.main.version-chap li, .chapters-list li, .row-content-chapter li, #manga-chapters-holder li, .l-chapters li, .eplister li, #chapterlist li, .chbox");
                if (chapterElements.isEmpty()) chapterElements = doc.select(".row-content-chapter a, .chapter-lieb a, .listing-chapters_wrap a, #manga-chapters-holder a[href*='chapter'], .eplister a, #chapterlist a");
                if (chapterElements.isEmpty()) {
                    try {
                        org.jsoup.nodes.Document ajaxDoc = org.jsoup.Jsoup.connect(mangaUrl + (mangaUrl.endsWith("/") ? "" : "/") + "ajax/chapters/")
                            .userAgent(com.fire.mangareader.network.MangaScraper.globalUserAgent)
                            .header("Cookie", com.fire.mangareader.network.MangaScraper.globalCookies)
                            .post();
                        chapterElements = ajaxDoc.select("li.wp-manga-chapter, .listing-chapters_wrap li, .row-content-chapter li, a[href*='chapter']");
                    } catch (Exception e) {}
                }
                if (chapterElements.isEmpty()) {
                    String mangaId = "";
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("manga_id'\\s*:\\s*'(\\d+)'").matcher(html);
                    if (m.find()) mangaId = m.group(1);
                    else {
                        m = java.util.regex.Pattern.compile("manga_id\\s*=\\s*(\\d+)").matcher(html);
                        if (m.find()) mangaId = m.group(1);
                        else {
                             org.jsoup.nodes.Element holder = doc.selectFirst("#manga-chapters-holder");
                             if (holder != null) {
                                 m = java.util.regex.Pattern.compile("data-id=\"(\\d+)\"").matcher(holder.outerHtml());
                                 if (m.find()) mangaId = m.group(1);
                             }
                        }
                    }
                    if (!mangaId.isEmpty()) {
                        try {
                            java.net.URL urlObj = new java.net.URL(mangaUrl);
                            String ajaxUrl = urlObj.getProtocol() + "://" + urlObj.getHost() + "/wp-admin/admin-ajax.php";
                            org.jsoup.nodes.Document ajaxDoc = org.jsoup.Jsoup.connect(ajaxUrl)
                                .userAgent(com.fire.mangareader.network.MangaScraper.globalUserAgent)
                                .header("Cookie", com.fire.mangareader.network.MangaScraper.globalCookies)
                                .data("action", "manga_get_chapters")
                                .data("manga", mangaId)
                                .post();
                            chapterElements = ajaxDoc.select("li.wp-manga-chapter, .listing-chapters_wrap li, .row-content-chapter li, a[href*='chapter']");
                        } catch (Exception e) {}
                    }
                }

                for (Element el : chapterElements) {
                    Element link = el.tagName().equals("a") ? el : el.select("a").first();
                    if (link != null) {
                        String cUrl = link.absUrl("href");
                        String cTitle = link.text().replace("\\t", "").replace("\\n", "").replace("\\r", "").trim();
                        if (cTitle.isEmpty()) {
                            Element span = link.select("span.chapternum, span.epicurve").first();
                            if(span != null) cTitle = span.text().replace("\\t", "").replace("\\n", "").replace("\\r", "").trim();
                        }
                        if (!cUrl.isEmpty() && !cUrl.startsWith("javascript") && !cUrl.equals("#") && !cTitle.isEmpty()) {
                            Chapter chapter = new Chapter();
                            chapter.setUrl(cUrl);
                            chapter.setTitle(cTitle);
                            chapters.add(chapter);
                        }
                    }
                }

                final String finalDesc = tempDescription;
                final String finalStatus = tempStatus;
                final String finalAuthor = tempAuthor;

                runOnUiThread(() -> {
                    if (chapters.size() != chapterList.size() || chapterList.isEmpty()) {
                        descriptionText.setText(finalDesc);
                        statusText.setText(finalStatus);
                        TextView authorText = findViewById(R.id.mangaAuthor);
                        if (authorText != null) authorText.setText(finalAuthor);
                        chapterList.clear();
                        chapterList.addAll(chapters);
                        chapterAdapter.notifyDataSetChanged();
                        
                        saveToRoomCache(finalDesc, finalStatus, chapters);
                    }
                    if (!isSilentBackgroundFetch) progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    
                    if (chapters.isEmpty() && !isSilentBackgroundFetch) Toast.makeText(MangaDetailActivity.this, "لم نتمكن من إيجاد الفصول، جرب إعادة فتح المانجا.", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                if (!isSilentBackgroundFetch) {
                    runOnUiThread(() -> {
                        Toast.makeText(MangaDetailActivity.this, "خطأ في تحليل البيانات", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            }
        }).start();
    }

    private void showMyListBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_my_list, null);
        dialog.setContentView(view);
        
        String[] statuses = {"اشاهدها حاليا", "ارغب بمشاهدتها", "تم مشاهدتها", "لا ارغب بمشاهدتها"};
        int[] ids = {R.id.statusWatching, R.id.statusPlan, R.id.statusCompleted, R.id.statusDropped};
        
        new Thread(() -> {
            LibraryItem item = AppDatabase.getInstance(this).mangaDao().getItemById(mangaUrl);
            final String currentStatus = (item != null && item.getStatus() != null) ? item.getStatus() : "";
            runOnUiThread(() -> {
                for (int i = 0; i < statuses.length; i++) {
                    final String status = statuses[i];
                    android.widget.TextView tv = view.findViewById(ids[i]);
                    if (status.equals(currentStatus)) {
                        tv.setTextColor(android.graphics.Color.parseColor("#FF9800"));
                    }
                    tv.setOnClickListener(v -> {
                        updateMangaStatus(status);
                        dialog.dismiss();
                    });
                }
            });
        }).start();
        dialog.show();
    }

    private void updateMangaStatus(String status) {
        new Thread(() -> {
            LibraryItem item = AppDatabase.getInstance(this).mangaDao().getItemById(mangaUrl);
            if (item == null) {
                item = new LibraryItem();
                item.setMangaId(mangaUrl);
                item.setTitle(mangaTitle);
                item.setCoverUrl(mangaCover);
                item.setAddedTime(System.currentTimeMillis());
            }
            item.setStatus(status);
            item.setFavorite(true);
            AppDatabase.getInstance(this).mangaDao().insert(item);
            
            isFavorite = true;
            runOnUiThread(() -> {
                ImageView btnFavorite = findViewById(R.id.btnFavorite);
        // btnFavoriteContainer = findViewById(R.id.btnFavoriteContainer);
        // btnCommentsContainer
        // tvFavoriteText
                if (btnFavorite != null) btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                android.widget.Toast.makeText(MangaDetailActivity.this, "تمت الإضافة إلى: " + status, android.widget.Toast.LENGTH_SHORT).show();
            });
        }).start();
    }
    private void checkFavoriteStatus() {
        if (!com.fire.mangareader.network.SupabaseManager.getInstance(this).isLoggedIn()) {
            runOnUiThread(() -> {
                if (btnFavorite != null) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                    btnFavorite.setColorFilter(android.graphics.Color.GRAY);
                }
                isFavorite = false;
                currentLibraryStatus = "";
            });
            return;
        }

        com.fire.mangareader.network.SupabaseManager.getInstance(this).checkLibraryStatus(mangaUrl, new com.fire.mangareader.network.SupabaseManager.DataCallback() {
            @Override
            public void onSuccess(org.json.JSONArray data) {
                boolean inLibrary = data != null && data.length() > 0;
                String status = "";
                if (inLibrary) {
                    try {
                        status = data.getJSONObject(0).getString("status");
                    } catch (Exception e) {}
                }
                
                final boolean isFav = inLibrary;
                final String finalStatus = status;
                
                runOnUiThread(() -> {
                    if (btnFavorite != null) {
                        btnFavorite.setImageResource(isFav ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                        btnFavorite.setColorFilter(isFav ? android.graphics.Color.RED : android.graphics.Color.GRAY);
                    }
                    isFavorite = isFav;
                    currentLibraryStatus = finalStatus;
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (btnFavorite != null) {
                        btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                        btnFavorite.setColorFilter(android.graphics.Color.GRAY);
                    }
                    isFavorite = false;
                    currentLibraryStatus = "";
                });
            }
        });
    }

    private void loadReadChapters() {
        new Thread(() -> {
            List<com.fire.mangareader.database.ChapterState> states = AppDatabase.getInstance(this).chapterStateDao().getAllStatesForManga(mangaUrl);
            List<String> downList = AppDatabase.getInstance(this).downloadDao().getDownloadedChapterUrls(mangaUrl);
            
            List<String> readUrls = new java.util.ArrayList<>();
            for (com.fire.mangareader.database.ChapterState state : states) {
                if (state.isRead) readUrls.add(state.chapterUrl);
            }
            
            runOnUiThread(() -> {
                if (chapterAdapter != null) {
                    chapterAdapter.setReadChapters(readUrls);
                    chapterAdapter.setDownloadedChapters(downList);
                }
            });
        }).start();
    }

    private void loadAniListMetadata() {
        if (mangaTitle == null || mangaTitle.isEmpty()) return;
        com.fire.mangareader.utils.AniListManager.fetchMetadata(mangaTitle, new com.fire.mangareader.utils.AniListManager.AniListCallback() {
            @Override
            public void onSuccess(com.fire.mangareader.model.AniListMetadata metadata) {
                runOnUiThread(() -> {
                    TextView tvGlobalRating = findViewById(R.id.tvGlobalRating);
                    TextView tvGlobalRatingCount = findViewById(R.id.tvGlobalRatingCount);
                    TextView tvALRating = findViewById(R.id.tvALRating);
                    TextView tvALRatingCount = findViewById(R.id.tvALRatingCount);
                    // TextView tvAniListFormat
                    // TextView tvAniListAuthor
                    // TextView tvAniListArtist
                    TextView tvAniListCountry = findViewById(R.id.tvAniListCountry);
                    // TextView tvAniListDates

                    if (tvALRating != null && metadata.averageScore > 0) {
                        double scoreOutOf10 = metadata.averageScore / 10.0;
                        tvALRating.setText(String.format(java.util.Locale.US, "%.1f/10", scoreOutOf10));
                    }
                    if (tvALRatingCount != null && metadata.popularity > 0) {
                        int pop = metadata.popularity;
                        tvALRatingCount.setText(pop >= 1000 ? (pop / 1000) + "K" : String.valueOf(pop));
                    }
                    if (false && metadata.format != null) {
                        // tvAniListFormat(metadata.format);
                    }
                    if (false) {
                        // tvAniListAuthor("المؤلف: " + (metadata.author != null && !metadata.author.isEmpty() ? metadata.author : "غير متوفر"));
                    }
                    if (false && metadata.artist != null) {
                        // tvAniListArtist("الرسام: " + metadata.artist);
                    }
                    if (tvAniListCountry != null && metadata.countryOfOrigin != null) {
                        tvAniListCountry.setText("دولة المنشأ: " + metadata.countryOfOrigin);
                    }
                    if (false) {
                        // tvAniListDates("الإصدار: " + metadata.getFormattedDates());
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    // Ignored
                });
                // Silently fallback to scraper data
            }
        });
    }

    private void setupRatingButtons() {
    }

    private void showMultiCriteriaRatingDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("تقييم " + mangaTitle);

        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        String[] criteria = {"التقييم العام (Overall)", "حبكة القصة (Story)", "الشخصيات (Characters)", "جودة الرسم (Art)"};
        final android.widget.RatingBar[] ratingBars = new android.widget.RatingBar[4];

        for (int i = 0; i < criteria.length; i++) {
            android.widget.TextView tv = new android.widget.TextView(this);
            tv.setText(criteria[i]);
            tv.setTextSize(14);
            tv.setPadding(0, 10, 0, 4);
            layout.addView(tv);

            android.widget.RatingBar rb = new android.widget.RatingBar(this, null, android.R.attr.ratingBarStyleSmall);
            rb.setNumStars(5);
            rb.setStepSize(0.5f);
            rb.setRating(4.0f);
            ratingBars[i] = rb;
            layout.addView(rb);
        }

        builder.setView(layout);
        builder.setPositiveButton("إرسال التقييم", (dialog, which) -> {
            float overall = ratingBars[0].getRating() * 2.0f; // Scale to 10
            float story = ratingBars[1].getRating() * 2.0f;
            float characters = ratingBars[2].getRating() * 2.0f;
            float art = ratingBars[3].getRating() * 2.0f;

            com.fire.mangareader.utils.GlobalMangaStatsManager.submitRating(MangaDetailActivity.this, mangaUrl, mangaTitle, overall, story, characters, art, new com.fire.mangareader.utils.GlobalMangaStatsManager.RatingCallback() {
                @Override
                public void onSuccess(double newAverage, int totalVotes) {
                    runOnUiThread(() -> {
                        TextView tvGlobalRating = findViewById(R.id.tvGlobalRating);
                        TextView tvGlobalRatingCount = findViewById(R.id.tvGlobalRatingCount);
                        if (tvGlobalRating != null) tvGlobalRating.setText(String.format(java.util.Locale.US, "%.1f/10", newAverage));
                        if (tvGlobalRatingCount != null) tvGlobalRatingCount.setText(String.valueOf(totalVotes));
                        Toast.makeText(MangaDetailActivity.this, "تم تسجيل تقييمك بنجاح! شكرًا لك ⭐", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> Toast.makeText(MangaDetailActivity.this, "تم حفظ تقييمك محلياً ✔️", Toast.LENGTH_SHORT).show());
                }
            });
        });
        builder.setNegativeButton("إلغاء", null);
        builder.show();
    }

    private void showRatingStatsDialog() {
        com.fire.mangareader.utils.GlobalMangaStatsManager.fetchStats(MangaDetailActivity.this, mangaUrl, new com.fire.mangareader.utils.GlobalMangaStatsManager.StatsCallback() {
            @Override
            public void onSuccess(com.fire.mangareader.utils.GlobalMangaStats stats) {
                runOnUiThread(() -> {
                    new androidx.appcompat.app.AlertDialog.Builder(MangaDetailActivity.this)
                        .setTitle("إحصائيات وتقييمات " + mangaTitle)
                        .setMessage("⭐ التقييم العام: " + String.format(java.util.Locale.US, "%.1f/10", stats.overallAverage) +
                                   "\n📖 القصة: " + String.format(java.util.Locale.US, "%.1f/10", stats.storyAverage) +
                                   "\n👤 الشخصيات: " + String.format(java.util.Locale.US, "%.1f/10", stats.charactersAverage) +
                                   "\n🎨 الرسم: " + String.format(java.util.Locale.US, "%.1f/10", stats.artAverage) +
                                   "\n\n👥 إجمالي المقيمين: " + stats.totalVotes)
                        .setPositiveButton("حسناً", null)
                        .show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(MangaDetailActivity.this, "جاري تجهيز الإحصائيات...", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void toggleFavorite() {
        if (!com.fire.mangareader.network.SupabaseManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "يجب تسجيل الدخول لإضافة المانجا للمكتبة", Toast.LENGTH_SHORT).show();
            return;
        }
        
        android.view.View targetView = btnFavoriteContainer != null ? btnFavoriteContainer : btnFavorite;
        targetView.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
            targetView.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            
            String[] options = {"أقرأها حالياً", "أرغب بمشاهدتها", "مكتملة", "مفضلة", "إزالة من القائمة"};
            String[] statusValues = {"reading", "plan_to_read", "completed", "favorite", "remove"};
            
            int checkedItem = -1;
            if (currentLibraryStatus.equals("reading")) checkedItem = 0;
            else if (currentLibraryStatus.equals("plan_to_read")) checkedItem = 1;
            else if (currentLibraryStatus.equals("completed")) checkedItem = 2;
            else if (currentLibraryStatus.equals("favorite")) checkedItem = 3;
            
            new androidx.appcompat.app.AlertDialog.Builder(MangaDetailActivity.this)
                    .setTitle("إضافة إلى المكتبة")
                    .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                        dialog.dismiss();
                        if (which == 4) {
                            // Remove
                            com.fire.mangareader.network.SupabaseManager.getInstance(MangaDetailActivity.this).removeFromLibrary(mangaUrl, new com.fire.mangareader.network.SupabaseManager.AuthCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    isFavorite = false;
                                    currentLibraryStatus = "";
                                    btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                                    btnFavorite.setColorFilter(android.graphics.Color.GRAY);
                                    Toast.makeText(MangaDetailActivity.this, "تمت الإزالة من المكتبة", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onError(String error) {
                                    Toast.makeText(MangaDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String selectedStatus = statusValues[which];
                            com.fire.mangareader.network.SupabaseManager.getInstance(MangaDetailActivity.this).addToLibrary(mangaUrl, mangaTitle, mangaCover, selectedStatus, new com.fire.mangareader.network.SupabaseManager.AuthCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    isFavorite = true;
                                    currentLibraryStatus = selectedStatus;
                                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                                    btnFavorite.setColorFilter(android.graphics.Color.RED);
                                    Toast.makeText(MangaDetailActivity.this, options[which], Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onError(String error) {
                                    Toast.makeText(MangaDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .show();
        }).start();
    }
}
