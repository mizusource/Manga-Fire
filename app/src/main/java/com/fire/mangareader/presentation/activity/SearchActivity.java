package com.fire.mangareader.presentation.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.shimmer.ShimmerFrameLayout;

import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fire.mangareader.R;
import com.fire.mangareader.presentation.adapter.MangaAdapter;
import com.fire.mangareader.domain.model.Manga;
import com.fire.mangareader.data.network.MangaScraper;
import com.fire.mangareader.data.network.CloudflareBypassDialog;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ShimmerFrameLayout shimmerView;
    private MangaAdapter adapter;
    private List<Manga> searchResults;
    private Chip chipGlobalSearch;
    private TextView tvSourceStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        com.fire.mangareader.data.network.MangaScraper.BASE_URL = com.fire.mangareader.data.network.SourceManager.getActiveSource(this);

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        shimmerView = findViewById(R.id.shimmerView);
        chipGlobalSearch = findViewById(R.id.chipGlobalSearch);
        tvSourceStatus = findViewById(R.id.tvSourceStatus);

        searchResults = new ArrayList<>();
        adapter = new MangaAdapter(this, searchResults);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);

        updateSourceStatusText();

        chipGlobalSearch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            updateSourceStatusText();
            if (searchView.getQuery() != null && !searchView.getQuery().toString().trim().isEmpty()) {
                performSearch(searchView.getQuery().toString().trim());
            }
        });

        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void updateSourceStatusText() {
        if (chipGlobalSearch.isChecked()) {
            tvSourceStatus.setText("جميع المصادر (Global)");
        } else {
            tvSourceStatus.setText(com.fire.mangareader.data.network.SourceManager.getActiveSourceName(this));
        }
    }

    private void performSearch(final String query) {
        if (query == null || query.trim().isEmpty()) return;

        shimmerView.setVisibility(View.VISIBLE);
        shimmerView.startShimmer();
        searchResults.clear();
        adapter.notifyDataSetChanged();

        MangaScraper.ScrapingCallback callback = new MangaScraper.ScrapingCallback() {
            @Override
            public void onSuccess(List<Manga> mangas) {
                runOnUiThread(() -> {
                    shimmerView.stopShimmer();
                    shimmerView.setVisibility(View.GONE);
                    if (mangas != null && !mangas.isEmpty()) {
                        searchResults.addAll(mangas);
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    if (errorMessage.contains("403") || errorMessage.contains("503") || errorMessage.contains("Cloudflare") || errorMessage.contains("Just a moment")) {
                        new CloudflareBypassDialog(SearchActivity.this, MangaScraper.BASE_URL, new CloudflareBypassDialog.BypassCallback() {
                            @Override
                            public void onSuccess(String cookies, String userAgent) {
                                performSearch(query); 
                            }

                            @Override
                            public void onFailed() {
                                shimmerView.stopShimmer();
                    shimmerView.setVisibility(View.GONE);
                                Toast.makeText(SearchActivity.this, "فشل تجاوز الحماية", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                    } else {
                        shimmerView.stopShimmer();
                    shimmerView.setVisibility(View.GONE);
                        Toast.makeText(SearchActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };

        if (chipGlobalSearch.isChecked()) {
            MangaScraper.searchAllSources(query, callback);
        } else {
            MangaScraper.searchManga(query, callback);
        }
    }
}
