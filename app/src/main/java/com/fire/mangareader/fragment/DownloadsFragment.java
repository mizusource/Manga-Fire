package com.fire.mangareader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.adapter.DownloadAdapter;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.database.DownloadedChapter;
import java.util.ArrayList;
import java.util.List;

public class DownloadsFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private DownloadAdapter adapter;
    private List<DownloadedChapter> downloadList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // تم ربط التصميم الجديد الخاص بالتنزيلات
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);

        recyclerView = view.findViewById(R.id.downloadsRecyclerView);
        tvEmpty = view.findViewById(R.id.tvEmptyDownloads);

        downloadList = new ArrayList<>();
        adapter = new DownloadAdapter(requireContext(), downloadList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        loadDownloads();

        return view;
    }

    private void loadDownloads() {
        new Thread(() -> {
            // جلب البيانات من قاعدة البيانات باستخدام الـ DAO الجديد
            List<DownloadedChapter> dbList = AppDatabase.getInstance(requireContext()).downloadDao().getAllDownloads();

            // تحديث واجهة المستخدم
            requireActivity().runOnUiThread(() -> {
                downloadList.clear();
                downloadList.addAll(dbList);
                adapter.notifyDataSetChanged();

                if (downloadList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            });
        }).start();
    }
}
