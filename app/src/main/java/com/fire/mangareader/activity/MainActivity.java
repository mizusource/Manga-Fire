package com.fire.mangareader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.MangaAdapter;
import com.fire.mangareader.model.Manga;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvLatestUpdates;
    private SwipeRefreshLayout swipeRefreshMain;
    private ProgressBar mainProgressBar;
    private MangaAdapter adapter;
    private List<Manga> mangaList;
    private androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    private com.google.android.material.navigation.NavigationView navView;
    private android.widget.ImageView btnMenuToggle;
 
    private String BASE_URL = "https://mangalik.net/";

    
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        com.fire.mangareader.network.MangaScraper.globalCookies = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("cloudflare_cookies", "");
        BASE_URL = com.fire.mangareader.network.SourceManager.getActiveSource(this);
        com.fire.mangareader.network.MangaScraper.BASE_URL = BASE_URL;

        rvLatestUpdates = findViewById(R.id.rvLatestUpdates);
        swipeRefreshMain = findViewById(R.id.swipeRefreshMain);
        mainProgressBar = findViewById(R.id.mainProgressBar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnMenuToggle = findViewById(R.id.btnMenuToggle);
   
        android.widget.ImageView btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, SearchActivity.class)));

        // فتح القائمة عند الضغط على زر الهمبرغر
        btnMenuToggle.setOnClickListener(v -> drawerLayout.openDrawer(androidx.core.view.GravityCompat.START));

        // برمجة الأزرار داخل القائمة الجانبية
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            
        if (id == R.id.nav_source) {
            showSourceSelectionDialog();
            drawerLayout.closeDrawers();
            return true;
        }
            if (id == R.id.nav_home) {
                // نحن في الرئيسية بالفعل
            } else if (id == R.id.nav_library) {
                // الانتقال لشاشة المكتبة
                startActivity(new Intent(MainActivity.this, LibraryActivity.class));
            } else if (id == R.id.nav_downloads) {
                // الانتقال لشاشة التنزيلات
                startActivity(new Intent(MainActivity.this, DownloadsActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_settings) {
                // الانتقال لشاشة الإعدادات
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }

            // إغلاق القائمة بعد اختيار أي عنصر
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
            return true;
        });

        rvLatestUpdates.setLayoutManager(new GridLayoutManager(this, 3));
        // Start Background Sync for Updates
        androidx.work.PeriodicWorkRequest updateRequest = new androidx.work.PeriodicWorkRequest.Builder(
                com.fire.mangareader.utils.UpdateCheckWorker.class, 12, java.util.concurrent.TimeUnit.HOURS)
                .build();
        androidx.work.WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "MangaUpdateCheck",
                androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                updateRequest);
        mangaList = new ArrayList<>();
        adapter = new MangaAdapter(this, mangaList);
        rvLatestUpdates.setAdapter(adapter);

        swipeRefreshMain.setOnRefreshListener(() -> loadHomePageViaWebView(true));
        loadHomePageViaWebView(false);
    }

    private void setupRecentReading() {
        android.view.View container = findViewById(R.id.recentReadingContainer);
        androidx.recyclerview.widget.RecyclerView rvRecent = findViewById(R.id.rvRecentReading);
        java.util.List<com.fire.mangareader.utils.RecentReadingManager.RecentItem> recentItems = com.fire.mangareader.utils.RecentReadingManager.getRecent(this);
        
        if (recentItems != null && !recentItems.isEmpty()) {
            container.setVisibility(android.view.View.VISIBLE);
            rvRecent.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            rvRecent.setAdapter(new com.fire.mangareader.adapter.RecentReadingAdapter(this, recentItems));
        } else {
            container.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecentReading();
    }

    private void loadHomePageViaWebView(boolean isSilentRefresh) {
        if (!isSilentRefresh) mainProgressBar.setVisibility(View.VISIBLE);

        android.view.ViewGroup rootView = findViewById(android.R.id.content);
        android.webkit.WebView webView = new android.webkit.WebView(this); webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);
        
        // 👻 جعل المتصفح بحجم بكسل واحد وشفاف تماماً لكي لا يلاحظه المستخدم
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

        webView.setWebChromeClient(new android.webkit.WebChromeClient());
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            
            
            public void onReceivedError(android.webkit.WebView view, android.webkit.WebResourceRequest request, android.webkit.WebResourceError error) { if(!request.isForMainFrame()) return; runOnUiThread(() -> { mainProgressBar.setVisibility(View.GONE); swipeRefreshMain.setRefreshing(false); Toast.makeText(MainActivity.this, "Network Error: " + error.getDescription(), Toast.LENGTH_SHORT).show(); });
                rootView.removeView(webView);
                try { webView.stopLoading(); webView.loadUrl("about:blank"); new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { try { webView.destroy(); } catch (Exception ignored2) {} }, 1500); } catch (Exception ignored) {}
            }
            public void onPageFinished(android.webkit.WebView view, String url) {
                String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                if (cookies != null) { com.fire.mangareader.network.MangaScraper.globalCookies = cookies; getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().putString("cloudflare_cookies", cookies).apply(); }
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { view.evaluateJavascript("(function() { return document.documentElement.outerHTML; })();", html -> {
                    if (html == null || html.equals("null")) return;

                    if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare") || html.contains("you have been blocked") || html.contains("cf-error-details")) { runOnUiThread(() -> { mainProgressBar.setVisibility(View.GONE); webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(1, 1)); webView.setAlpha(0.01f); Toast.makeText(MainActivity.this, "يرجى الانتظار لتخطي حماية Cloudflare...", Toast.LENGTH_LONG).show(); });
                        return; 
                    }

                    rootView.removeView(webView); try { webView.stopLoading(); webView.loadUrl("about:blank"); new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { try { webView.destroy(); } catch (Exception ignored2) {} }, 1500); } catch (Exception ignored) {} 
                    String cleanHtml = html.replaceAll("^\"|\"$", "").replace("\\u003C", "<").replace("\\u003E", ">").replace("\\\"", "\"").replace("\\n", " ").replace("\\t", " ").replace("\\\\", "");
                    
                    parseHtmlLocally(cleanHtml, isSilentRefresh);
                }); }, 2500);
            }
        });
        
        webView.loadUrl(BASE_URL);
    }

    private void showSourceSelectionDialog() {
        String[] sources = {"Manga Lik (mangalik.net)", "Manga-Starz (manga-starz.net)", "Mangatek (mangatek.com)", "Mangasid (mangasid.com)"};
        String[] urls = {com.fire.mangareader.network.SourceManager.SOURCE_MANGALIK, com.fire.mangareader.network.SourceManager.SOURCE_MANGA_STARZ, com.fire.mangareader.network.SourceManager.SOURCE_MANGATEK, com.fire.mangareader.network.SourceManager.SOURCE_MANGASID};
        int checkedItem = 0;
        String activeSource = com.fire.mangareader.network.SourceManager.getActiveSource(this);
        for (int i = 0; i < urls.length; i++) { if (activeSource.equals(urls[i])) checkedItem = i; }
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("اختر مصدر المانجا")
            .setSingleChoiceItems(sources, checkedItem, (dialog, which) -> {
                com.fire.mangareader.network.SourceManager.setActiveSource(MainActivity.this, urls[which]);
                BASE_URL = urls[which];
                com.fire.mangareader.network.MangaScraper.BASE_URL = BASE_URL;
                dialog.dismiss();
                
                android.view.Menu menu = ((com.google.android.material.navigation.NavigationView) findViewById(R.id.nav_view)).getMenu();
                android.view.MenuItem sourceItem = menu.findItem(R.id.nav_source);
                if (sourceItem != null) sourceItem.setTitle("المصدر: " + com.fire.mangareader.network.SourceManager.getActiveSourceName(MainActivity.this));
                
                swipeRefreshMain.setRefreshing(true);
                loadHomePageViaWebView(true);
            })
            .show();
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
                    
                    mainProgressBar.setVisibility(View.GONE);
                    swipeRefreshMain.setRefreshing(false);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    mainProgressBar.setVisibility(View.GONE);
                    swipeRefreshMain.setRefreshing(false);
                    Toast.makeText(MainActivity.this, "حدث خطأ في قراءة بيانات الموقع", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
