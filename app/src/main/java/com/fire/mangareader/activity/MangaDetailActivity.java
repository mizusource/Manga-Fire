package com.fire.mangareader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.bumptech.glide.Glide;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.ChapterAdapter;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.database.LibraryItem;
import com.fire.mangareader.model.Chapter;
import com.fire.mangareader.network.MangalikScraper;
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
    private FloatingActionButton fabFavorite, fabComments;
    private FloatingActionButton btnRead;
    private Chapter nextChapterToRead = null;
    private String mangaUrl, mangaTitle, mangaCover;
    private ChapterAdapter chapterAdapter;
    private List<Chapter> chapterList;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manga_detail);

        mangaUrl = getIntent().getStringExtra("mangaUrl");
        mangaTitle = getIntent().getStringExtra("mangaTitle");
        mangaCover = getIntent().getStringExtra("mangaCover");

        coverImage = findViewById(R.id.mangaCover);
        coverImageBlur = findViewById(R.id.mangaCoverBlur);
        titleText = findViewById(R.id.mangaTitle);
        statusText = findViewById(R.id.mangaStatus);
        descriptionText = findViewById(R.id.mangaDescription);
        chaptersRecycler = findViewById(R.id.chaptersRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); 
        fabFavorite = findViewById(R.id.fabFavorite);
        fabComments = findViewById(R.id.fabComments);
        btnRead = findViewById(R.id.btnRead);

        titleText.setText(mangaTitle);
        
        Glide.with(this).load(mangaCover).override(15, 15).into(coverImageBlur);
        Glide.with(this).load(mangaCover).into(coverImage);

        chapterList = new ArrayList<>();
        chapterAdapter = new ChapterAdapter(this, chapterList, mangaUrl);
        chaptersRecycler.setLayoutManager(new LinearLayoutManager(this));
        chaptersRecycler.setAdapter(chapterAdapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadMangaDetailsViaWebView(true);
        });

        loadReadChapters();
        checkFavoriteStatus();
        checkRoomCacheAndLoad();

        fabFavorite.setOnClickListener(v -> toggleFavorite());

        fabComments.setOnClickListener(v -> {
            Intent intent = new Intent(MangaDetailActivity.this, CommentsActivity.class);
            intent.putExtra("mangaUrl", mangaUrl);
            startActivity(intent);
        });

        btnRead.setOnClickListener(v -> {
            if (nextChapterToRead != null) {
                new Thread(() -> {
                    com.fire.mangareader.database.ChapterState state = new com.fire.mangareader.database.ChapterState();
                    state.chapterUrl = nextChapterToRead.getUrl();
                    state.mangaUrl = mangaUrl;
                    state.isRead = true;
                    AppDatabase.getInstance(MangaDetailActivity.this).chapterStateDao().insert(state);
                }).start();

                Intent intent = new Intent(MangaDetailActivity.this, ChapterReaderActivity.class);
                intent.putExtra("chapterUrl", nextChapterToRead.getUrl());
                intent.putExtra("mangaUrl", mangaUrl);
                startActivity(intent);
            }
        });
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

    private void updateSmartReadButton(List<com.fire.mangareader.database.ChapterState> states) {
        if (chapterList == null || chapterList.isEmpty()) {
            // 🚀 تم الاستغناء عن النص وجعل الزر باهتاً في حال عدم وجود فصول
            btnRead.setEnabled(false);
            btnRead.setAlpha(0.5f);
            return;
        }
        
        btnRead.setEnabled(true);
        btnRead.setAlpha(1.0f);
        nextChapterToRead = null;

        java.util.Map<String, com.fire.mangareader.database.ChapterState> stateMap = new java.util.HashMap<>();
        for (com.fire.mangareader.database.ChapterState s : states) stateMap.put(s.chapterUrl, s);

        for (int i = chapterList.size() - 1; i >= 0; i--) {
            Chapter currentChapter = chapterList.get(i);
            com.fire.mangareader.database.ChapterState state = stateMap.get(currentChapter.getUrl());
            
            if (state == null || !state.isCompleted) {
                nextChapterToRead = currentChapter;
                break;
            }
        }

        if (nextChapterToRead == null) {
            nextChapterToRead = chapterList.get(0);
        }
    }

    private void loadMangaDetailsViaWebView(boolean isSilentBackgroundFetch) {
        if (!isSilentBackgroundFetch) progressBar.setVisibility(View.VISIBLE);

        android.view.ViewGroup rootView = findViewById(android.R.id.content);
        android.webkit.WebView webView = new android.webkit.WebView(this);
        
        webView.setLayoutParams(new android.view.ViewGroup.LayoutParams(1, 1));
        webView.setAlpha(0.0f); 
        rootView.addView(webView);

        android.webkit.WebSettings settings = webView.getSettings();
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
        settings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        settings.setMediaPlaybackRequiresUserGesture(false); 

        String agent = "Mozilla/5.0 (Linux; Android 14; SM-A366B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36";
        settings.setUserAgentString(agent);
        MangalikScraper.globalUserAgent = agent; 

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                view.evaluateJavascript("(function() { return document.documentElement.outerHTML; })();", html -> {
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

                    if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare")) {
                        return; 
                    }

                    rootView.removeView(webView); 
                    
                    String cleanHtml = html.replaceAll("^\"|\"$", "").replace("\\u003C", "<").replace("\\\"", "\"").replace("\\n", " ").replace("\\t", " ").replace("\\r", " ").replace("\\\\", "");
                    
                    parseHtmlLocally(cleanHtml, isSilentBackgroundFetch);
                });
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
                if (statusElement != null) {
                    String statusText = statusElement.text();
                    if (statusText.contains("مكتمل") || statusText.contains("Completed") || statusText.contains("End") || statusText.contains("انتهى")) {
                        tempStatus = "مكتملة";
                    }
                }

                List<Chapter> chapters = new ArrayList<>();
                Elements chapterElements = doc.select("li.wp-manga-chapter, .listing-chapters_wrap li, ul.main.version-chap li, .chapters-list li, .row-content-chapter li, #manga-chapters-holder li, .l-chapters li, .eplister li, #chapterlist li, .chbox");
                if (chapterElements.isEmpty()) chapterElements = doc.select(".row-content-chapter a, .chapter-lieb a, .listing-chapters_wrap a, #manga-chapters-holder a[href*='chapter'], .eplister a, #chapterlist a");

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

                runOnUiThread(() -> {
                    if (chapters.size() != chapterList.size() || chapterList.isEmpty()) {
                        descriptionText.setText(finalDesc);
                        statusText.setText("الحالة: " + finalStatus);
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

    private void checkFavoriteStatus() {
        new Thread(() -> {
            isFavorite = AppDatabase.getInstance(this).mangaDao().isFavorite(mangaUrl);
            runOnUiThread(() -> fabFavorite.setImageResource(isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off));
        }).start();
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
                updateSmartReadButton(states);
            });
        }).start();
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        fabFavorite.setImageResource(isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        new Thread(() -> {
            LibraryItem item = new LibraryItem();
            item.setMangaId(mangaUrl);
            item.setTitle(mangaTitle);
            item.setCoverUrl(mangaCover);
            item.setFavorite(isFavorite);
            item.setAddedTime(System.currentTimeMillis());
            if (isFavorite) AppDatabase.getInstance(this).mangaDao().insert(item);
            else {
                AppDatabase.getInstance(this).mangaDao().setFavorite(mangaUrl, false);
                AppDatabase.getInstance(this).mangaDao().cleanOrphans();
            }
        }).start();
        Toast.makeText(this, isFavorite ? "تمت الإضافة للمفضلة" : "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
    }
}
