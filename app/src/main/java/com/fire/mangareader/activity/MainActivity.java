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
    private androidx.drawerlayout.widget.DrawerLayout drawerLayout;
    private com.google.android.material.navigation.NavigationView navView;
    private android.widget.ImageView btnMenuToggle;
 
    private String BASE_URL = "https://mangalik.net/";

    
    
    @Override
    protected void onResume() {
        super.onResume();
        updateNavHeader();
    }
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        com.fire.mangareader.utils.DisplayUtils.optimizeRefreshRate(this);
        setContentView(R.layout.activity_main);
        com.fire.mangareader.network.MangaScraper.globalCookies = getSharedPreferences("AppPrefs", MODE_PRIVATE).getString("cloudflare_cookies", "");
        BASE_URL = com.fire.mangareader.network.SourceManager.getActiveSource(this);
        com.fire.mangareader.network.MangaScraper.BASE_URL = BASE_URL;

        
        rvLatestUpdates = findViewById(R.id.rvLatestUpdates);
        vpHeroBanner = findViewById(R.id.vpHeroBanner);

        swipeRefreshMain = findViewById(R.id.swipeRefreshMain);
        mainShimmerView = findViewById(R.id.mainShimmerView);
        btnToggleView = findViewById(R.id.btnToggleView);
        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        btnMenuToggle = findViewById(R.id.btnMenuToggle);
   
        android.widget.ImageView btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, SearchActivity.class)));

        // فتح القائمة عند الضغط على زر الهمبرغر
        btnMenuToggle.setOnClickListener(v -> {
            updateNavHeader();
            drawerLayout.openDrawer(androidx.core.view.GravityCompat.START);
        });

        // برمجة الأزرار داخل القائمة الجانبية
        navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            
            if (id == R.id.nav_home) {
                // نحن في الرئيسية بالفعل
            } else if (id == R.id.nav_currently_reading) {
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                intent.putExtra("FILTER_STATUS", "اشاهدها حاليا");
                startActivity(intent);
            } else if (id == R.id.nav_want_to_read) {
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                intent.putExtra("FILTER_STATUS", "ارغب بمشاهدتها");
                startActivity(intent);
            } else if (id == R.id.nav_completed) {
                Intent intent = new Intent(MainActivity.this, LibraryActivity.class);
                intent.putExtra("FILTER_STATUS", "تم مشاهدتها");
                startActivity(intent);
            } else if (id == R.id.nav_downloads) {
                // الانتقال لشاشة التنزيلات
                startActivity(new Intent(MainActivity.this, DownloadsActivity.class));
            } else if (id == R.id.nav_favorites) {
                // الانتقال للمفضلة
                startActivity(new Intent(MainActivity.this, LibraryActivity.class));
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_admin) {
                startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
            } else if (id == R.id.nav_settings) {
                // الانتقال لشاشة الإعدادات
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            } else if (id == R.id.nav_telegram) {
                com.fire.mangareader.utils.TelegramManager.openTelegramChannel(MainActivity.this, "wv_sj");
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
            
            
            public void onReceivedError(android.webkit.WebView view, android.webkit.WebResourceRequest request, android.webkit.WebResourceError error) { if(!request.isForMainFrame()) return; runOnUiThread(() -> { if(mainShimmerView != null) {
                            mainShimmerView.stopShimmer();
                            mainShimmerView.setVisibility(View.GONE);
                        } swipeRefreshMain.setRefreshing(false); Toast.makeText(MainActivity.this, "Network Error: " + error.getDescription(), Toast.LENGTH_SHORT).show(); });
                rootView.removeView(webView);
                try { webView.stopLoading(); webView.loadUrl("about:blank"); new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { try { webView.destroy(); } catch (Exception ignored2) {} }, 1500); } catch (Exception ignored) {}
            }
            public void onPageFinished(android.webkit.WebView view, String url) {
                String cookies = android.webkit.CookieManager.getInstance().getCookie(url);
                if (cookies != null) { com.fire.mangareader.network.MangaScraper.globalCookies = cookies; getSharedPreferences("AppPrefs", MODE_PRIVATE).edit().putString("cloudflare_cookies", cookies).apply(); }
                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> { view.evaluateJavascript("(function() { return document.documentElement.outerHTML; })();", html -> {
                    if (html == null || html.equals("null")) return;

                    if (html.contains("Just a moment...") || html.contains("cf-browser-verification") || html.contains("Cloudflare") || html.contains("you have been blocked") || html.contains("cf-error-details")) { runOnUiThread(() -> { if(mainShimmerView != null) {
                            mainShimmerView.stopShimmer();
                            mainShimmerView.setVisibility(View.GONE);
                        } webView.setLayoutParams(new android.widget.FrameLayout.LayoutParams(1, 1)); webView.setAlpha(0.01f); Toast.makeText(MainActivity.this, "يرجى الانتظار لتخطي حماية Cloudflare...", Toast.LENGTH_LONG).show(); });
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

    private void updateNavHeader() {
        android.view.View headerView = navView.getHeaderView(0);
        if (headerView != null) {
            android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);
            android.widget.ImageView btnNotifications = headerView.findViewById(R.id.btnNotifications);
            android.widget.TextView navHeaderName = headerView.findViewById(R.id.navHeaderName);
            android.widget.TextView navHeaderEmail = headerView.findViewById(R.id.navHeaderEmail);
            android.widget.ImageView navHeaderImage = headerView.findViewById(R.id.navHeaderImage);
            
            if (btnNotifications != null) {
                btnNotifications.setOnClickListener(v -> {
                    android.widget.Toast.makeText(MainActivity.this, "لا توجد إشعارات حالياً", android.widget.Toast.LENGTH_SHORT).show();
                });
            }

            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                navHeaderName.setText(user.getDisplayName() != null && !user.getDisplayName().isEmpty() ? user.getDisplayName() : "المستخدم");
                navHeaderEmail.setText(user.getEmail());
                headerView.setOnClickListener(null);
                
                com.fire.mangareader.utils.PreferenceManager pm = new com.fire.mangareader.utils.PreferenceManager(this);
                if (pm.getProfilePic() != null && !pm.getProfilePic().isEmpty()) {
                    navHeaderImage.setColorFilter(null);
                    com.bumptech.glide.Glide.with(this).load(android.net.Uri.parse(pm.getProfilePic())).circleCrop().into(navHeaderImage);
                } else if (user.getPhotoUrl() != null) {
                    navHeaderImage.setColorFilter(null);
                    com.bumptech.glide.Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(navHeaderImage);
                } else {
                    navHeaderImage.setImageResource(R.drawable.ic_drawer_profile);
                    navHeaderImage.setColorFilter(android.graphics.Color.GRAY);
                }
                
                if (btnEditProfile != null) {
                    btnEditProfile.setVisibility(android.view.View.VISIBLE);
                    btnEditProfile.setOnClickListener(v -> {
                        startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class));
                    });
                }
                
                android.view.Menu menu = navView.getMenu();
                android.view.MenuItem adminItem = menu.findItem(R.id.nav_admin);
                if (adminItem != null) {
                    adminItem.setVisible(user.getEmail() != null && user.getEmail().equals("mstfybdwy633@gmail.com"));
                }
            } else {
                navHeaderName.setText("تسجيل الدخول");
                navHeaderEmail.setText("انقر هنا لتسجيل الدخول");
                navHeaderImage.setImageResource(R.drawable.ic_drawer_profile);
                navHeaderImage.setColorFilter(android.graphics.Color.GRAY);
                headerView.setOnClickListener(v -> {
                    startActivity(new android.content.Intent(MainActivity.this, LoginActivity.class));
                });
                
                if (btnEditProfile != null) {
                    btnEditProfile.setVisibility(android.view.View.GONE);
                }
                
                android.view.Menu menu = navView.getMenu();
                android.view.MenuItem adminItem = menu.findItem(R.id.nav_admin);
                if (adminItem != null) adminItem.setVisible(false);
            }
        }
    }
}