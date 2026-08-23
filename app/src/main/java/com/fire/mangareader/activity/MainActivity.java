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
 
    private final String BASE_URL = "https://mangalik.net/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            
            if (id == R.id.nav_home) {
                // نحن في الرئيسية بالفعل
            } else if (id == R.id.nav_favorites) {
                // الانتقال لشاشة المكتبة
                startActivity(new Intent(MainActivity.this, LibraryActivity.class));
            } else if (id == R.id.nav_downloads) {
                // الانتقال لشاشة التنزيلات
                startActivity(new Intent(MainActivity.this, DownloadsActivity.class));
            } else if (id == R.id.nav_settings) {
                // الانتقال لشاشة الإعدادات
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }

            // إغلاق القائمة بعد اختيار أي عنصر
            drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START);
            return true;
        });

        rvLatestUpdates.setLayoutManager(new GridLayoutManager(this, 3));
        mangaList = new ArrayList<>();
        adapter = new MangaAdapter(this, mangaList);
        rvLatestUpdates.setAdapter(adapter);

        swipeRefreshMain.setOnRefreshListener(() -> loadHomePageViaWebView(true));
        loadHomePageViaWebView(false);
    }

    private void loadHomePageViaWebView(boolean isSilentRefresh) {
        if (!isSilentRefresh) mainProgressBar.setVisibility(View.VISIBLE);

        android.view.ViewGroup rootView = findViewById(android.R.id.content);
        android.webkit.WebView webView = new android.webkit.WebView(this);
        
        // 👻 جعل المتصفح بحجم بكسل واحد وشفاف تماماً لكي لا يلاحظه المستخدم
        webView.setLayoutParams(new android.view.ViewGroup.LayoutParams(1, 1));
        webView.setAlpha(0.0f); 
        rootView.addView(webView);

        android.webkit.WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        
        String agent = "Mozilla/5.0 (Linux; Android 14; SM-A366B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36";
        settings.setUserAgentString(agent);

        android.webkit.CookieManager cookieManager = android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public void onPageFinished(android.webkit.WebView view, String url) {
                view.evaluateJavascript("(function() { return document.documentElement.outerHTML; })();", html -> {
                    if (html == null || html.equals("null")) return;

                    if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare")) {
                        return; 
                    }

                    rootView.removeView(webView); 
                    String cleanHtml = html.replaceAll("^\"|\"$", "").replace("\\u003C", "<").replace("\\\"", "\"").replace("\\n", " ").replace("\\t", " ").replace("\\\\", "");
                    
                    parseHtmlLocally(cleanHtml, isSilentRefresh);
                });
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
