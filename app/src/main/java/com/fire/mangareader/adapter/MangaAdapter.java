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
import com.fire.mangareader.activity.MangaDetailActivity;
import com.fire.mangareader.model.Manga;
import java.util.List;

public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.MangaViewHolder> {
    private Context context;
    private List<Manga> mangaList;

    public MangaAdapter(Context context, List<Manga> mangaList) {
        this.context = context;
        this.mangaList = mangaList;
    }

    @NonNull
    @Override
    public MangaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manga_grid, parent, false);
        return new MangaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MangaViewHolder holder, int position) {
        Manga manga = mangaList.get(position);
        
        holder.tvMangaTitle.setText(manga.getTitle());
        holder.tvLatestChapter.setText(manga.getLatestChapter() != null && !manga.getLatestChapter().isEmpty() ? manga.getLatestChapter() : "مستمرة");
        holder.tvRating.setText(manga.getRating() != null && !manga.getRating().isEmpty() ? manga.getRating() : "N/A");

        // تحميل صورة الغلاف عبر مكتبة Glide
        Glide.with(context)
             .load(manga.getCoverUrl())
             .override(300, 400)
             .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
             .format(com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565) // تقليل الدقة قليلاً لتسريع التحميل وتوفير الرام
             .into(holder.mangaCover);

        // عند النقر على المانجا، افتح شاشة التفاصيل
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MangaDetailActivity.class);
            intent.putExtra("mangaUrl", manga.getUrl());
            intent.putExtra("mangaTitle", manga.getTitle());
            intent.putExtra("mangaCover", manga.getCoverUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mangaList != null ? mangaList.size() : 0;
    }

    static class MangaViewHolder extends RecyclerView.ViewHolder {
        ImageView mangaCover;
        TextView tvMangaTitle, tvLatestChapter, tvRating;

        public MangaViewHolder(@NonNull View itemView) {
            super(itemView);
            mangaCover = itemView.findViewById(R.id.mangaCover);
            tvMangaTitle = itemView.findViewById(R.id.tvMangaTitle);
            tvLatestChapter = itemView.findViewById(R.id.tvLatestChapter);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}
