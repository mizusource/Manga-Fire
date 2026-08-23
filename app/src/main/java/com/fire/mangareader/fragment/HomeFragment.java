package com.fire.mangareader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.MangaAdapter;
import com.fire.mangareader.model.Manga;
import com.fire.mangareader.network.MangaScraper;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private MangaAdapter adapter;
    private List<Manga> mangaList;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        progressBar = view.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mangaList = new ArrayList<>();
        adapter = new MangaAdapter(getContext(), mangaList);
        recyclerView.setAdapter(adapter);

        swipeRefresh.setOnRefreshListener(this::loadMangas);
        loadMangas();

        return view;
    }

    private void loadMangas() {
        swipeRefresh.setRefreshing(true);
        progressBar.setVisibility(View.VISIBLE);

        MangaScraper.fetchLatestManga(new MangaScraper.ScrapingCallback() {
            @Override
            public void onSuccess(List<Manga> mangas) {
                mangaList.clear();
                mangaList.addAll(mangas);
                adapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(String errorMessage) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
                swipeRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
