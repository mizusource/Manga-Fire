package com.fire.mangareader.presentation.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fire.mangareader.R;
import com.fire.mangareader.domain.model.Manga;
import com.fire.mangareader.presentation.adapter.MangaAdapter;

import java.util.ArrayList;
import java.util.List;

public class CustomListDetailActivity extends AppCompatActivity {

    private RecyclerView rvManga;
    private MangaAdapter adapter;
    private List<Manga> mangaList = new ArrayList<>();
    private LinearLayout emptyStateLayout;
    private String listId;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list_detail);

        listId = getIntent().getStringExtra("listId");
        listName = getIntent().getStringExtra("listName");

        TextView tvListTitle = findViewById(R.id.tvListTitle);
        if (listName != null) {
            tvListTitle.setText(listName);
        }

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvManga = findViewById(R.id.rvManga);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        adapter = new MangaAdapter(this, mangaList);
        rvManga.setLayoutManager(new GridLayoutManager(this, 3));
        rvManga.setAdapter(adapter);

        loadManga();
    }

    private void loadManga() {
        if (listId == null) return;
        new Thread(() -> {
            try {
                List<String> mangaUrls = com.fire.mangareader.data.local.AppDatabase.Companion.getDatabase(this).customListDao().getMangaUrlsForList(listId);
                List<Manga> loadedManga = new ArrayList<>();
                for (String url : mangaUrls) {
                    com.fire.mangareader.data.database.LibraryItem item = com.fire.mangareader.data.database.AppDatabase.getInstance(this).mangaDao().getItemById(url);
                    if (item != null) {
                        Manga manga = new Manga();
                        manga.setUrl(item.getMangaId());
                        manga.setTitle(item.getTitle());
                        manga.setCoverUrl(item.getCoverUrl());
                        loadedManga.add(manga);
                    }
                }
                
                runOnUiThread(() -> {
                    mangaList.clear();
                    mangaList.addAll(loadedManga);
                    adapter.notifyDataSetChanged();
                    
                    if (mangaList.isEmpty()) {
                        emptyStateLayout.setVisibility(View.VISIBLE);
                        rvManga.setVisibility(View.GONE);
                    } else {
                        emptyStateLayout.setVisibility(View.GONE);
                        rvManga.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
