package com.fire.mangareader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.MangaAdapter;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.database.LibraryItem;
import com.fire.mangareader.model.Manga;
import java.util.ArrayList;
import java.util.List;

public class LibraryActivity extends AppCompatActivity {

    private RecyclerView rvLibrary;
    private TextView tvEmptyState;
    private MangaAdapter adapter;
    private List<Manga> favoriteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        // إعداد زر الرجوع
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        rvLibrary = findViewById(R.id.rvLibrary);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // عرض 3 مانجات في كل صف (مثل الرئيسية)
        rvLibrary.setLayoutManager(new GridLayoutManager(this, 3));
        favoriteList = new ArrayList<>();
        adapter = new MangaAdapter(this, favoriteList);
        rvLibrary.setAdapter(adapter);
    }

    // نستخدم onResume لكي يتم تحديث القائمة تلقائياً إذا أزلت مانجا من المفضلة وعدت للشاشة
    @Override
    protected void onResume() {
        super.onResume();
        loadFavoritesFromDatabase();
    }

    private void loadFavoritesFromDatabase() {
        new Thread(() -> {
            try {
                // سحب البيانات من Room Database
                List<LibraryItem> items = AppDatabase.getInstance(this).mangaDao().getAllFavorites();
                
                List<Manga> mappedList = new ArrayList<>();
                for (LibraryItem item : items) {
                    if (item.isFavorite()) {
                        Manga manga = new Manga();
                        manga.setTitle(item.getTitle());
                        manga.setUrl(item.getMangaId());
                        manga.setCoverUrl(item.getCoverUrl());
                        manga.setRating("❤️"); // وضع قلب كتقييم لتمييزها
                        manga.setLatestChapter("مفضلة"); 
                        mappedList.add(manga);
                    }
                }

                // تحديث الواجهة
                runOnUiThread(() -> {
                    favoriteList.clear();
                    favoriteList.addAll(mappedList);
                    adapter.notifyDataSetChanged();

                    // إظهار أو إخفاء رسالة "المكتبة فارغة"
                    if (favoriteList.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                        rvLibrary.setVisibility(View.GONE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                        rvLibrary.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
