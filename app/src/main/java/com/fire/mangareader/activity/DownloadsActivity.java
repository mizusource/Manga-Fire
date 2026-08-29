package com.fire.mangareader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.DownloadAdapter;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.database.DownloadedChapter;
import java.util.ArrayList;
import java.util.List;

public class DownloadsActivity extends AppCompatActivity {

    private RecyclerView rvDownloads;
    private View emptyStateLayout;
    private DownloadAdapter adapter;
    private List<DownloadedChapter> downloadedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        setupBottomNavigation();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvDownloads = findViewById(R.id.rvDownloads);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        View btnExplore = findViewById(R.id.btnExplore);
        if (btnExplore != null) btnExplore.setOnClickListener(v -> finish());

        rvDownloads.setLayoutManager(new LinearLayoutManager(this));
        downloadedList = new ArrayList<>();
        adapter = new DownloadAdapter(this, downloadedList);
        rvDownloads.setAdapter(adapter);

        loadDownloadsFromDatabase();
    }

    private void loadDownloadsFromDatabase() {
        new Thread(() -> {
            try {
                List<DownloadedChapter> items = AppDatabase.getInstance(this).downloadDao().getAllDownloads();
                
                runOnUiThread(() -> {
                    downloadedList.clear();
                    downloadedList.addAll(items);
                    adapter.notifyDataSetChanged();

                    if (downloadedList.isEmpty()) {
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        rvDownloads.setVisibility(View.GONE);
                    } else {
                        emptyStateLayout.setVisibility(View.GONE);
                        rvDownloads.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;
        bottomNav.setSelectedItemId(R.id.nav_downloads);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_downloads) {
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new android.content.Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return false;
            } else if (id == R.id.nav_library) {
                startActivity(new android.content.Intent(this, LibraryActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return false;
            } else if (id == R.id.nav_profile) {
                startActivity(new android.content.Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return false;
            }
            return false;
        });
    }
}
