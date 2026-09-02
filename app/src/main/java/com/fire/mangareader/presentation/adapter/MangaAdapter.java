package com.fire.mangareader.presentation.adapter;

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
import com.fire.mangareader.presentation.activity.MangaDetailActivity;
import com.fire.mangareader.domain.model.Manga;
import java.util.List;

public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.MangaViewHolder> {
    private Context context;
    private List<Manga> mangaList;
    private boolean isListView = false;

    public void setListView(boolean isListView) {
        this.isListView = isListView;
        notifyDataSetChanged();
    }

    public MangaAdapter(Context context, List<Manga> mangaList) {
        this.context = context;
        this.mangaList = mangaList;
    }

    @NonNull
    @Override
    public MangaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isListView ? R.layout.item_manga_list : R.layout.item_manga_grid;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
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
             .format(com.bumptech.glide.load.DecodeFormat.PREFER_RGB_565)
             .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
             .into(holder.mangaCover);

        // Set transition name for the shared element
        androidx.core.view.ViewCompat.setTransitionName(holder.mangaCover, "cover_transition_" + manga.getUrl());

        // عند النقر على المانجا، افتح شاشة التفاصيل
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MangaDetailActivity.class);
            intent.putExtra("mangaUrl", manga.getUrl());
            intent.putExtra("mangaTitle", manga.getTitle());
            intent.putExtra("mangaCover", manga.getCoverUrl());
            
            if (context instanceof android.app.Activity) {
                androidx.core.app.ActivityOptionsCompat options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (android.app.Activity) context, holder.mangaCover, "cover_transition_" + manga.getUrl()
                );
                context.startActivity(intent, options.toBundle());
            } else {
                context.startActivity(intent);
            }
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
