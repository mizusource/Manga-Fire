package com.fire.mangareader.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.fire.mangareader.R;
import java.util.List;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.fire.mangareader.util.SystemUtils;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {
    private Context context;
    private List<String> imageUrls;

    public PageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
                int maxTexture = SystemUtils.getMaxTextureSize();
        Glide.with(context)
             .load(imageUrls.get(position))
             .downsample(DownsampleStrategy.AT_MOST)
             .override(maxTexture, maxTexture)
             .into(holder.pageImageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView pageImageView;
        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            pageImageView = itemView.findViewById(R.id.pageImageView);
        }
    }
}
