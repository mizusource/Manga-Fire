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
    private TextView tvEmptyState;
    private DownloadAdapter adapter;
    private List<DownloadedChapter> downloadedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvDownloads = findViewById(R.id.rvDownloads);
        tvEmptyState = findViewById(R.id.tvEmptyState);

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
                        tvEmptyState.setVisibility(View.VISIBLE);
                        rvDownloads.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        rvDownloads.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
