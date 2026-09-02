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
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private MangaAdapter adapter;
    private List<Manga> mangaList;
    private TextView tvEmpty;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        tvEmpty.setText("لم تقرأ أي مانجا بعد");

        mangaList = new ArrayList<>();
        adapter = new MangaAdapter(requireContext(), mangaList);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        AppDatabase.getInstance(requireContext()).mangaDao().getHistory()
                .observe(getViewLifecycleOwner(), items -> {
                    mangaList.clear();
                    if (items != null && !items.isEmpty()) {
                        for (LibraryItem item : items) {
                            Manga m = new Manga();
                            m.setUrl(item.getMangaId());
                            m.setTitle(item.getTitle());
                            m.setCoverUrl(item.getCoverUrl());
                            mangaList.add(m);
                        }
                        tvEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                });

        return view;
    }
}
