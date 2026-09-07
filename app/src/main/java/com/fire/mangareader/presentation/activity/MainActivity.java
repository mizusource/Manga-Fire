package com.fire.mangareader.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.fire.mangareader.R;
import com.fire.mangareader.data.network.MangaScraper;
import com.fire.mangareader.domain.model.Manga;
import com.fire.mangareader.presentation.adapter.HeroBannerAdapter;
import com.fire.mangareader.presentation.adapter.MangaAdapter;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView recyclerView;
    private MangaAdapter adapter;
    private List<Manga> mangaList;

    private ViewPager2 heroViewPager;
    private HeroBannerAdapter heroAdapter;
    private List<Manga> heroList;

    private SwipeRefreshLayout swipeRefresh;
    private View mainShimmerView;

    private Handler sliderHandler = new Handler(Looper.getMainLooper());
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (heroViewPager != null && heroAdapter != null && heroAdapter.getItemCount() > 0) {
                int currentItem = heroViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % heroAdapter.getItemCount();
                heroViewPager.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        recyclerView = findViewById(R.id.rvLatestUpdates);
        heroViewPager = findViewById(R.id.vpHeroBanner);
        swipeRefresh = findViewById(R.id.swipeRefreshMain);
        mainShimmerView = findViewById(R.id.mainShimmerView);
        
        ImageView btnMenuToggle = findViewById(R.id.btnMenuToggle);
        if (btnMenuToggle != null) {
            btnMenuToggle.setOnClickListener(v -> {
                if (drawerLayout != null) drawerLayout.openDrawer(GravityCompat.START);
            });
        }

        ImageView btnSearch = findViewById(R.id.btnSearch);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        }

        setupRecyclerView();
        setupHeroBanner();
        setupNavigation();

        swipeRefresh.setOnRefreshListener(this::loadMangas);
        loadMangas();
    }

    private void setupRecyclerView() {
        mangaList = new ArrayList<>();
        adapter = new MangaAdapter(this, mangaList);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
    }

    private void setupHeroBanner() {
        heroList = new ArrayList<>();
        heroAdapter = new HeroBannerAdapter(this, heroList);
        heroViewPager.setAdapter(heroAdapter);
    }

    private void setupNavigation() {
        if (navigationView == null) return;
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already here
            } else if (id == R.id.nav_currently_reading || id == R.id.nav_want_to_read || id == R.id.nav_completed || id == R.id.nav_favorites) {
                Intent intent = new Intent(this, LibraryActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_downloads) {
                startActivity(new Intent(this, DownloadsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_admin) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            }
            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });
    }

    private void loadMangas() {
        swipeRefresh.setRefreshing(true);
        if (mainShimmerView != null) mainShimmerView.setVisibility(View.VISIBLE);
        
        MangaScraper.fetchLatestManga(new MangaScraper.ScrapingCallback() {
            @Override
            public void onSuccess(List<Manga> mangas) {
                mangaList.clear();
                heroList.clear();

                if (mangas.size() > 5) {
                    heroList.addAll(mangas.subList(0, 5));
                    mangaList.addAll(mangas.subList(5, mangas.size()));
                } else {
                    mangaList.addAll(mangas);
                }

                heroAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();

                sliderHandler.removeCallbacks(sliderRunnable);
                if (heroList.size() > 1) {
                    sliderHandler.postDelayed(sliderRunnable, 3000);
                }

                swipeRefresh.setRefreshing(false);
                if (mainShimmerView != null) mainShimmerView.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                swipeRefresh.setRefreshing(false);
                if (mainShimmerView != null) mainShimmerView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (heroList != null && heroList.size() > 1) {
            sliderHandler.postDelayed(sliderRunnable, 3000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}
