package com.fire.mangareader.presentation.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.presentation.adapter.MangaAdapter;
import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.data.database.LibraryItem;
import com.fire.mangareader.domain.model.Manga;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private RecyclerView recyclerView;
    private MangaAdapter adapter;
    private List<Manga> mangaList;
    private List<LibraryItem> rawItems = new ArrayList<>();
    private TextView tvEmpty;
    private View categoryFilterScroll;
    private ChipGroup chipGroupCategories;
    private String selectedFilter = "all";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvEmpty.setText("لا توجد عناصر في مكتبتك بعد");

        categoryFilterScroll = view.findViewById(R.id.categoryFilterScroll);
        if (categoryFilterScroll != null) {
            categoryFilterScroll.setVisibility(View.VISIBLE);
        }

        chipGroupCategories = view.findViewById(R.id.chipGroupCategories);
        if (chipGroupCategories != null) {
            chipGroupCategories.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) return;
                int id = checkedIds.get(0);
                if (id == R.id.chipAll) selectedFilter = "all";
                else if (id == R.id.chipReading) selectedFilter = "اشاهدها حاليا";
                else if (id == R.id.chipPlan) selectedFilter = "ارغب بمشاهدتها";
                else if (id == R.id.chipCompleted) selectedFilter = "تم مشاهدتها";
                else if (id == R.id.chipFavorites) selectedFilter = "favorite";
                applyFilter();
            });
        }

        mangaList = new ArrayList<>();
        adapter = new MangaAdapter(requireContext(), mangaList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        AppDatabase.getInstance(requireContext()).mangaDao().getAllLibraryItems()
                .observe(getViewLifecycleOwner(), items -> {
                    rawItems.clear();
                    if (items != null) rawItems.addAll(items);
                    applyFilter();
                });

        return view;
    }

    private void applyFilter() {
        mangaList.clear();
        for (LibraryItem item : rawItems) {
            boolean matches = false;
            if ("all".equals(selectedFilter)) {
                matches = true;
            } else if ("favorite".equals(selectedFilter)) {
                matches = item.isFavorite();
            } else if (item.getStatus() != null && item.getStatus().equals(selectedFilter)) {
                matches = true;
            }

            if (matches) {
                Manga m = new Manga();
                m.setUrl(item.getMangaId());
                m.setTitle(item.getTitle());
                m.setCoverUrl(item.getCoverUrl());
                mangaList.add(m);
            }
        }

        if (mangaList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            if (!"all".equals(selectedFilter)) {
                tvEmpty.setText("لا توجد عناصر في تصنيف: " + selectedFilter);
            } else {
                tvEmpty.setText("لا توجد مانجا في المفضلة أو القائمة");
            }
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }
}
