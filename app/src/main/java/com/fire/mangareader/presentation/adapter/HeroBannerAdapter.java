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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fire.mangareader.R;
import com.fire.mangareader.presentation.activity.MangaDetailActivity;
import com.fire.mangareader.domain.model.Manga;

import java.util.List;

public class HeroBannerAdapter extends RecyclerView.Adapter<HeroBannerAdapter.ViewHolder> {

    private Context context;
    private List<Manga> mangaList;

    public HeroBannerAdapter(Context context, List<Manga> mangaList) {
        this.context = context;
        this.mangaList = mangaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hero_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Manga manga = mangaList.get(position);

        holder.tvHeroTitle.setText(manga.getTitle());
        String subtitle = manga.getLatestChapter();
        if (subtitle != null && !subtitle.isEmpty()) {
            holder.tvHeroSubtitle.setText("آخر التحديثات: " + subtitle);
        } else {
            holder.tvHeroSubtitle.setText("موصى به لك");
        }

        Glide.with(context)
                .load(manga.getCoverUrl())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                .into(holder.ivHeroCover);

        // Set transition name
        androidx.core.view.ViewCompat.setTransitionName(holder.ivHeroCover, "cover_transition_" + manga.getUrl());

        View.OnClickListener clickListener = v -> {
            Intent intent = new Intent(context, MangaDetailActivity.class);
            intent.putExtra("mangaUrl", manga.getUrl());
            intent.putExtra("mangaTitle", manga.getTitle());
            intent.putExtra("mangaCover", manga.getCoverUrl());
            
            if (context instanceof android.app.Activity) {
                androidx.core.app.ActivityOptionsCompat options = androidx.core.app.ActivityOptionsCompat.makeSceneTransitionAnimation(
                    (android.app.Activity) context, holder.ivHeroCover, "cover_transition_" + manga.getUrl()
                );
                context.startActivity(intent, options.toBundle());
            } else {
                context.startActivity(intent);
            }
        };

        holder.itemView.setOnClickListener(clickListener);
        holder.btnReadNow.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return mangaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHeroCover;
        TextView tvHeroTitle, tvHeroSubtitle;
        View btnReadNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHeroCover = itemView.findViewById(R.id.ivHeroCover);
            tvHeroTitle = itemView.findViewById(R.id.tvHeroTitle);
            tvHeroSubtitle = itemView.findViewById(R.id.tvHeroSubtitle);
            btnReadNow = itemView.findViewById(R.id.btnReadNow);
        }
    }
}
