package com.fire.mangareader.presentation.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.domain.model.NotificationModel;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final Context context;
    private final List<NotificationModel> list;

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel model = list.get(position);
        holder.tvSender.setText(model.senderName != null ? model.senderName : "تنبيه");
        holder.tvMessage.setText(model.message != null ? model.message : "");
        holder.itemView.setBackgroundColor(model.isRead ? Color.TRANSPARENT : Color.parseColor("#1A00E5FF"));
        
        holder.itemView.setOnClickListener(v -> {
            if (model.mangaUrl != null && !model.mangaUrl.isEmpty()) {
                Intent intent = new Intent(context, com.fire.mangareader.presentation.activity.MangaDetailActivity.class);
                intent.putExtra("MANGA_URL", model.mangaUrl);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSender, tvMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
