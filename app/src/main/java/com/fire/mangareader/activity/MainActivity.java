package com.fire.mangareader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.facebook.shimmer.ShimmerFrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.ImageView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.MangaAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.fire.mangareader.adapter.HeroBannerAdapter;
import com.fire.mangareader.model.Manga;
import com.fire.mangareader.utils.TelegramManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView rvLatestUpdates;
    private ViewPager2 vpHeroBanner;
    private SwipeRefreshLayout swipeRefreshMain;
    private ShimmerFrameLayout mainShimmerView;
    private ImageView btnToggleView;
    private boolean isListView = false;
    private MangaAdapter adapter;
    private List<Manga> mangaList;
    private String BASE_URL = "https://mangalik.net/";

    @Override
    protected void onResume() {
        super.onResume();
        updateProfileAvatar();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        com.fire.mangareader.utils.DisplayUtils.optimizeRefreshRate(this);
        setContentView(R.layout.activity_main);
        setupBottomNavigation();

        com.fire.mangareader.network.MangaScraper.globalCookies = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("cloudflare_cookies", "");
        BASE_URL = com.fire.mangareader.network.SourceManager.getActiveSource(this);
        com.fire.mangareader.network.MangaScraper.BASE_URL = BASE_URL;

        rvLatestUpdates = findViewById(R.id.rvLatestUpdates);
        vpHeroBanner = findViewById(R.id.vpHeroBanner);
        swipeRefreshMain = findViewById(R.id.swipeRefreshMain);
        mainShimmerView = findViewById(R.id.mainShimmerView);
        btnToggleView = findViewById(R.id.btnToggleView);

        android.widget.ImageView btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, SearchActivity.class)));

        rvLatestUpdates.setLayoutManager(new GridLayoutManager(this, 3));

        androidx.work.PeriodicWorkRequest updateRequest = new androidx.work.PeriodicWorkRequest.Builder(
                com.fire.mangareader.utils.UpdateCheckWorker.class, 12, java.util.concurrent.TimeUnit.HOURS)
                .build();
        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "MangaUpdateCheck",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                updateRequest);
        com.fire.mangareader.activity.StorageManagerActivity.autoCleanOldCache(this);

        mangaList = new ArrayList<>();
        adapter = new MangaAdapter(this, mangaList);
        rvLatestUpdates.setAdapter(adapter);

        btnToggleView.setOnClickListener(v -> {
            isListView = !isListView;
            if (isListView) {
                btnToggleView.setImageResource(android.R.drawable.ic_menu_gallery);
                rvLatestUpdates.setLayoutManager(new LinearLayoutManager(this));
            } else {
                btnToggleView.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                rvLatestUpdates.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 3));
            }
            adapter.setListView(isListView);
        });
        swipeRefreshMain.setOnRefreshListener(() -> loadHomePageViaWebView(true));
        loadHomePageViaWebView(false);
    }

    private void loadHomePageViaWebView(boolean isSilentRefresh) {
        if (!isSilentRefresh) mainShimmerView.setVisibility(View.VISIBLE);
        mainShimmerView.startShimmer();
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
        
        String agent = android.webkit.WebSettings.getDefaultUserAgent(this);
        settings.setUserAgentString(agent);
        com.fire.mangareader.network.MangaScraper.globalUserAgent = agent;

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                super.onPageFinished(view, url);
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    view.evaluateJavascript("(function() { return document.documentElement.outerHTML; })();", html -> {
                        if (html == null || html.equals("null")) return;
                        if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare") || html.contains("you have been blocked") || html.contains("cf-error-details")) { 
                            runOnUiThread(() -> { 
                                if(mainShimmerView != null) {
                                    mainShimmerView.stopShimmer();
                                    mainShimmerView.setVisibility(View.GONE);
                                } 
                                webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(1, 1)); 
                                webView.setAlpha(0.01f); 
                                Toast.makeText(MainActivity.this, "يرجى الانتظار لتخطي حماية Cloudflare...", Toast.LENGTH_LONG).show(); 
                            });
                            return; 
                        }
                        rootView.removeView(webView); 
                        try { 
                            webView.stopLoading(); 
                            webView.loadUrl("about:blank"); 
                            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { 
                                try { webView.destroy(); } catch (Exception ignored2) {} 
                            }, 1500); 
                        } catch (Exception ignored) {} 
                        
                        String cleanHtml = html.replaceAll("^\"|\"$", "").replace("\\u003C", "<").replace("\\u003E", ">").replace("\\\"", "\"").replace("\\n", " ").replace("\\t", " ").replace("\\\\", "");
                        parseHtmlLocally(cleanHtml, isSilentRefresh);
                    }); 
                }, 2500);
            }
        });
        webView.loadUrl(BASE_URL);
    }

    private void parseHtmlLocally(String html, boolean isSilentRefresh) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.parse(html, BASE_URL);
                List<Manga> fetchedList = new ArrayList<>();
                Elements items = doc.select(".page-item-detail, .manga-item, .bsx, .item, .c-tabs-item__content");
                for (Element item : items) {
                    Manga manga = new Manga();
                    Element titleEl = item.select("h3 a, .post-title a, .tt, a.manga-title").first();
                    if (titleEl != null) {
                        manga.setTitle(titleEl.text().trim());
                        manga.setUrl(titleEl.absUrl("href"));
                    } else {
                        continue;
                    }
                    Element imgEl = item.select("img").first();
                    if (imgEl != null) {
                        String imgUrl = imgEl.attr("data-src").isEmpty() ? imgEl.attr("src") : imgEl.attr("data-src");
                        manga.setCoverUrl(imgUrl);
                    }
                    Element chapEl = item.select(".chapter-item .chapter a, .epxs, .list-chapter a, .font-meta").first();
                    if (chapEl != null) {
                        manga.setLatestChapter(chapEl.text().trim());
                    }
                    Element ratingEl = item.select(".score, .numscore, .rating").first();
                    if (ratingEl != null) {
                        manga.setRating(ratingEl.text().trim());
                    }
                    fetchedList.add(manga);
                }
                runOnUiThread(() -> {
                    if (!fetchedList.isEmpty()) {
                        mangaList.clear();
                        mangaList.addAll(fetchedList);
                        adapter.notifyDataSetChanged();
                    } else {
                        if (!isSilentRefresh) Toast.makeText(MainActivity.this, "لم نتمكن من جلب الفصول الرئيسية.", Toast.LENGTH_SHORT).show();
                    }
                    if(mainShimmerView != null) {
                        mainShimmerView.stopShimmer();
                        mainShimmerView.setVisibility(View.GONE);
                    }
                    swipeRefreshMain.setRefreshing(false);
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    if(mainShimmerView != null) {
                        mainShimmerView.stopShimmer();
                        mainShimmerView.setVisibility(View.GONE);
                    }
                    swipeRefreshMain.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "حدث خطأ في قراءة بيانات الموقع", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_library) {
                startActivity(new android.content.Intent(MainActivity.this, LibraryActivity.class));
                overridePendingTransition(0, 0);
                return false;
            } else if (id == R.id.nav_downloads) {
                startActivity(new android.content.Intent(MainActivity.this, DownloadsActivity.class));
                overridePendingTransition(0, 0);
                return false;
            } else if (id == R.id.nav_profile) {
                startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                return false;
            }
            return false;
        });
        
        android.widget.ImageView btnProfileAvatar = findViewById(R.id.btnProfileAvatar);
        if (btnProfileAvatar != null) {
            btnProfileAvatar.setOnClickListener(v -> {
                com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class));
                } else {
                    startActivity(new android.content.Intent(MainActivity.this, LoginActivity.class));
                }
            });
        }
    }

    private void updateProfileAvatar() {
        android.widget.ImageView btnProfileAvatar = findViewById(R.id.btnProfileAvatar);
        if (btnProfileAvatar == null) return;
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            com.fire.mangareader.utils.PreferenceManager pm = new com.fire.mangareader.utils.PreferenceManager(this);
            if (pm.getProfilePic() != null && !pm.getProfilePic().isEmpty()) {
                com.bumptech.glide.Glide.with(this).load(android.net.Uri.parse(pm.getProfilePic())).circleCrop().into(btnProfileAvatar);
            } else if (user.getPhotoUrl() != null) {
                com.bumptech.glide.Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(btnProfileAvatar);
            }
        } else {
            btnProfileAvatar.setImageResource(R.drawable.ic_drawer_profile);
        }
    }
}
