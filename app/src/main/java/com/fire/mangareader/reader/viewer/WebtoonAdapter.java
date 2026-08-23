package com.fire.mangareader.reader.viewer;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WebtoonAdapter extends RecyclerView.Adapter<WebtoonPageHolder> {
    private List<String> pages = new ArrayList<>();
    private String cookies = "";
    private String refererUrl = "";

    public void setPages(List<String> pages, String cookies, String refererUrl) {
        this.pages = pages;
        this.cookies = cookies;
        this.refererUrl = refererUrl;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WebtoonPageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return new WebtoonPageHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull WebtoonPageHolder holder, int position) {
        holder.bind(pages.get(position), cookies, refererUrl);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }
}
