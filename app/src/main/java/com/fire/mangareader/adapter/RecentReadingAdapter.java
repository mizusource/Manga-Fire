package com.fire.mangareader.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fire.mangareader.R;
import com.fire.mangareader.activity.ChapterReaderActivity;
import com.fire.mangareader.utils.RecentReadingManager.RecentItem;

import java.util.List;

public class RecentReadingAdapter extends RecyclerView.Adapter<RecentReadingAdapter.ViewHolder> {
    private Context context;
    private List<RecentItem> items;

    public RecentReadingAdapter(Context context, List<RecentItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_reading, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentItem item = items.get(position);
        holder.tvTitle.setText(item.mangaTitle);
        holder.tvChapter.setText(item.chapterTitle);
        Glide.with(context).load(item.mangaCover).into(holder.imgCover);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChapterReaderActivity.class);
            intent.putExtra("mangaUrl", item.mangaUrl);
            intent.putExtra("mangaTitle", item.mangaTitle);
            intent.putExtra("mangaCover", item.mangaCover);
            intent.putExtra("chapterUrl", item.chapterUrl);
            intent.putExtra("chapterTitle", item.chapterTitle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCover;
        TextView tvTitle, tvChapter;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCover = itemView.findViewById(R.id.imgCover);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvChapter = itemView.findViewById(R.id.tvChapter);
        }
    }
}
