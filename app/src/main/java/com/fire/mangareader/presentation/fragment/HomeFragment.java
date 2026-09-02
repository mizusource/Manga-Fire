package com.fire.mangareader.presentation.fragment;

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
import androidx.viewpager2.widget.ViewPager2;
import com.fire.mangareader.presentation.adapter.HeroBannerAdapter;
import com.fire.mangareader.R;
import com.fire.mangareader.presentation.adapter.MangaAdapter;
import com.fire.mangareader.domain.model.Manga;
import com.fire.mangareader.data.network.MangaScraper;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private MangaAdapter adapter;
    private List<Manga> mangaList;
        private ViewPager2 heroViewPager;
    private View heroContainer;
    private HeroBannerAdapter heroAdapter;
    private List<Manga> heroList;
    private android.os.Handler sliderHandler = new android.os.Handler(android.os.Looper.getMainLooper());
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
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
                heroViewPager = view.findViewById(R.id.heroViewPager);
        heroContainer = view.findViewById(R.id.heroContainer);

        heroList = new ArrayList<>();
        heroAdapter = new HeroBannerAdapter(getContext(), heroList);
        heroViewPager.setAdapter(heroAdapter);
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

    
    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    private void loadMangas() {
        swipeRefresh.setRefreshing(true);
        progressBar.setVisibility(View.VISIBLE);

        MangaScraper.fetchLatestManga(new MangaScraper.ScrapingCallback() {

            @Override
            public void onSuccess(List<Manga> mangas) {
                mangaList.clear();
                heroList.clear();
                
                if (mangas.size() > 5) {
                    heroList.addAll(mangas.subList(0, 5));
                    mangaList.addAll(mangas.subList(5, mangas.size()));
                    heroContainer.setVisibility(View.VISIBLE);
                } else {
                    mangaList.addAll(mangas);
                    heroContainer.setVisibility(View.GONE);
                }
                
                                
                heroAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();
                
                sliderHandler.removeCallbacks(sliderRunnable);
                if (heroList.size() > 1) {
                    sliderHandler.postDelayed(sliderRunnable, 3000);
                }

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
