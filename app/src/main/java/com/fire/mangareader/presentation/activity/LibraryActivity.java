package com.fire.mangareader.presentation.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fire.mangareader.R;
import com.fire.mangareader.presentation.adapter.MangaAdapter;
import com.fire.mangareader.domain.model.Manga;
import com.fire.mangareader.data.network.SupabaseManager;
import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.data.database.LibraryItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {
    private RecyclerView rvLibrary;
    private View emptyStateLayout;
    private MangaAdapter adapter;
    private List<Manga> allLibraryItems;
    private List<Manga> displayList;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("مكتبتي");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvLibrary = findViewById(R.id.rvLibrary);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tabLayout = findViewById(R.id.tabLayout);
        
        View btnExplore = findViewById(R.id.btnExplore);
        if (btnExplore != null) btnExplore.setOnClickListener(v -> finish());

        rvLibrary.setLayoutManager(new GridLayoutManager(this, 3));
        allLibraryItems = new ArrayList<>();
        displayList = new ArrayList<>();
        adapter = new MangaAdapter(this, displayList);
        rvLibrary.setAdapter(adapter);

        setupTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLibraryFromSupabase();
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("الكل"));
        tabLayout.addTab(tabLayout.newTab().setText("أقرأها"));
        tabLayout.addTab(tabLayout.newTab().setText("سأقرأها"));
        tabLayout.addTab(tabLayout.newTab().setText("مكتملة"));
        tabLayout.addTab(tabLayout.newTab().setText("مفضلة"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterList(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void loadLibraryFromSupabase() {
        // 1. Load from local Room DB first (Offline-first approach)
        new Thread(() -> {
            java.util.List<com.fire.mangareader.data.database.LibraryItem> localItems = AppDatabase.getInstance(LibraryActivity.this).mangaDao().getAllItems();
            List<Manga> mappedList = new ArrayList<>();
            for (com.fire.mangareader.data.database.LibraryItem item : localItems) {
                if (!item.isFavorite() && (item.getStatus() == null || item.getStatus().isEmpty() || item.getStatus().equals("null"))) continue;
                Manga manga = new Manga();
                manga.setTitle(item.getTitle());
                manga.setUrl(item.getMangaId());
                manga.setCoverUrl(item.getCoverUrl());
                manga.setFavorite(item.isFavorite());
                manga.setStoryStatus(item.getStatus() == null ? "" : item.getStatus());
                
                String statusAr = "";
                if (item.getStatus() != null) {
                    if (item.getStatus().equals("reading")) statusAr = "أقرأها حالياً";
                    else if (item.getStatus().equals("plan_to_read")) statusAr = "سأقرأها";
                    else if (item.getStatus().equals("completed")) statusAr = "مكتملة";
                }
                manga.setLatestChapter(statusAr);
                mappedList.add(manga);
            }
            
            runOnUiThread(() -> {
                allLibraryItems.clear();
                allLibraryItems.addAll(mappedList);
                filterList(tabLayout.getSelectedTabPosition());
            });

            // 2. Fetch from Supabase and update local DB if logged in
            if (com.fire.mangareader.data.network.SupabaseManager.getInstance(LibraryActivity.this).isLoggedIn()) {
                com.fire.mangareader.data.network.SupabaseManager.getInstance(LibraryActivity.this).getUserLibrary(new com.fire.mangareader.data.network.SupabaseManager.DataCallback() {
                    @Override
                    public void onSuccess(org.json.JSONArray data) {
                        new Thread(() -> {
                            try {
                                for (int i = 0; i < data.length(); i++) {
                                    org.json.JSONObject obj = data.getJSONObject(i);
                                    String mangaId = obj.getString("manga_url");
                                    String title = obj.optString("manga_title", "مجهول");
                                    String cover = obj.optString("cover_url", "");
                                    String status = obj.optString("status", "");
                                    
                                    com.fire.mangareader.data.database.LibraryItem item = AppDatabase.getInstance(LibraryActivity.this).mangaDao().getItemById(mangaId);
                                    if (item == null) {
                                        item = new com.fire.mangareader.data.database.LibraryItem();
                                        item.setMangaId(mangaId);
                                        item.setAddedTime(System.currentTimeMillis());
                                    }
                                    item.setTitle(title);
                                    item.setCoverUrl(cover);
                                    if (status.equals("favorite")) {
                                        item.setFavorite(true);
                                    } else {
                                        item.setStatus(status);
                                    }
                                    AppDatabase.getInstance(LibraryActivity.this).mangaDao().insert(item);
                                }
                                
                                // Refresh UI after sync
                                java.util.List<com.fire.mangareader.data.database.LibraryItem> syncedItems = AppDatabase.getInstance(LibraryActivity.this).mangaDao().getAllItems();
                                List<Manga> freshList = new ArrayList<>();
                                for (com.fire.mangareader.data.database.LibraryItem item : syncedItems) {
                                    if (!item.isFavorite() && (item.getStatus() == null || item.getStatus().isEmpty() || item.getStatus().equals("null"))) continue;
                                    Manga manga = new Manga();
                                    manga.setTitle(item.getTitle());
                                    manga.setUrl(item.getMangaId());
                                    manga.setCoverUrl(item.getCoverUrl());
                                    manga.setFavorite(item.isFavorite());
                                    manga.setStoryStatus(item.getStatus() == null ? "" : item.getStatus());
                                    
                                    String statusAr = "";
                                    if (item.getStatus() != null) {
                                        if (item.getStatus().equals("reading")) statusAr = "أقرأها حالياً";
                                        else if (item.getStatus().equals("plan_to_read")) statusAr = "سأقرأها";
                                        else if (item.getStatus().equals("completed")) statusAr = "مكتملة";
                                    }
                                    manga.setLatestChapter(statusAr);
                                    freshList.add(manga);
                                }
                                runOnUiThread(() -> {
                                    allLibraryItems.clear();
                                    allLibraryItems.addAll(freshList);
                                    filterList(tabLayout.getSelectedTabPosition());
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    @Override
                    public void onError(String error) {
                        // Ignore, rely on cache
                    }
                });
            }
        }).start();
    }

    private void filterList(int tabPosition) {
        displayList.clear();
        String targetStatus = "";
        if (tabPosition == 1) targetStatus = "reading";
        else if (tabPosition == 2) targetStatus = "plan_to_read";
        else if (tabPosition == 3) targetStatus = "completed";

        for (Manga manga : allLibraryItems) {
            if (tabPosition == 0) {
                displayList.add(manga);
            } else if (tabPosition == 4) {
                if (manga.isFavorite()) displayList.add(manga);
            } else {
                if (manga.getStoryStatus() != null && manga.getStoryStatus().equals(targetStatus)) {
                    displayList.add(manga);
                }
            }
        }
        adapter.notifyDataSetChanged();
        if (displayList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvLibrary.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            rvLibrary.setVisibility(View.VISIBLE);
        }
    }
}
