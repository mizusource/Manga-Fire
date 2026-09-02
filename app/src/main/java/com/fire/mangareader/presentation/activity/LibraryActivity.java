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
        if (!SupabaseManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "يرجى تسجيل الدخول لعرض المكتبة", Toast.LENGTH_SHORT).show();
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvLibrary.setVisibility(View.GONE);
            return;
        }

        SupabaseManager.getInstance(this).getUserLibrary(new SupabaseManager.DataCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                List<Manga> mappedList = new ArrayList<>();
                try {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        Manga manga = new Manga();
                        manga.setTitle(obj.optString("manga_title", "مجهول"));
                        manga.setUrl(obj.getString("manga_url"));
                        manga.setCoverUrl(obj.optString("cover_url", ""));
                        manga.setRating("❤️"); // Can change based on status
                        
                        String status = obj.optString("status", "reading");
                        String statusAr = status;
                        if (status.equals("reading")) statusAr = "أقرأها حالياً";
                        else if (status.equals("plan_to_read")) statusAr = "سأقرأها";
                        else if (status.equals("completed")) statusAr = "مكتملة";
                        else if (status.equals("favorite")) statusAr = "مفضلة";
                        
                        manga.setLatestChapter(statusAr);
                        
                        // We can store original status in some unused field or just rely on latestChapter for filtering
                        // Let's store original status in 'rating' temporarily or extend Manga model.
                        manga.setRating("❤️"); 
                        
                        mappedList.add(manga);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                allLibraryItems.clear();
                allLibraryItems.addAll(mappedList);
                filterList(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LibraryActivity.this, "فشل جلب المكتبة", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterList(int tabPosition) {
        displayList.clear();
        String targetStatusAr = "";
        if (tabPosition == 1) targetStatusAr = "أقرأها حالياً";
        else if (tabPosition == 2) targetStatusAr = "سأقرأها";
        else if (tabPosition == 3) targetStatusAr = "مكتملة";
        else if (tabPosition == 4) targetStatusAr = "مفضلة";

        for (Manga manga : allLibraryItems) {
            if (tabPosition == 0 || (manga.getLatestChapter() != null && manga.getLatestChapter().equals(targetStatusAr))) {
                displayList.add(manga);
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
