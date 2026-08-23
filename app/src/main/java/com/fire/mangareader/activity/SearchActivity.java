package com.fire.mangareader.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.MangaAdapter;
import com.fire.mangareader.model.Manga;
import com.fire.mangareader.network.MangaScraper;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private MangaAdapter adapter;
    private List<Manga> searchResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        com.fire.mangareader.network.MangaScraper.BASE_URL = com.fire.mangareader.network.SourceManager.getActiveSource(this);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        searchResults = new ArrayList<>();
        adapter = new MangaAdapter(this, searchResults);

        // عرض النتائج في شبكة (Grid) من عمودين لتناسب تصميم بطاقة المانجا
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        // التركيز التلقائي على مربع البحث لتسهيل الكتابة فور فتح الشاشة
        searchView.requestFocus();

        // مراقبة ما يكتبه المستخدم في شريط البحث
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus(); // إخفاء لوحة المفاتيح
                performSearch(query);    // بدء عملية البحث
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void performSearch(String query) {
        if (query == null || query.trim().isEmpty()) return;

        // إظهار دائرة التحميل وتفريغ النتائج القديمة
        progressBar.setVisibility(View.VISIBLE);
        searchResults.clear();
        adapter.notifyDataSetChanged();

        // استخدام دالة البحث الموجودة في ملفك والتي تعتمد على Callback
        MangaScraper.searchManga(query, new MangaScraper.ScrapingCallback() {
            @Override
            public void onSuccess(List<Manga> mangas) {
                progressBar.setVisibility(View.GONE);
                if (mangas != null && !mangas.isEmpty()) {
                    searchResults.addAll(mangas);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(String errorMessage) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
