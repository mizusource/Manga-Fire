package com.fire.mangareader.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.domain.model.Comment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    private final Context context;
    private final List<Comment> replies;

    public ReplyAdapter(Context context, List<Comment> replies) {
        this.context = context;
        this.replies = replies;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reply, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment reply = replies.get(position);
        holder.tvUsername.setText(reply.username != null ? reply.username : "User");
        holder.tvCommentText.setText(reply.text != null ? reply.text : "");
        
        // Parse time and calculate time ago
        if (reply.created_at != null && !reply.created_at.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdf.parse(reply.created_at);
                if (date != null) {
                    holder.tvDate.setText(com.fire.mangareader.util.TimeUtils.getTimeAgo(date.getTime()));
                }
            } catch (ParseException e) {
                holder.tvDate.setText("");
            }
        } else {
            holder.tvDate.setText("");
        }

        holder.tvLikeCount.setText(String.valueOf(reply.likes));

        holder.btnLike.setOnClickListener(v -> {
            if (reply.id != null) {
                com.fire.mangareader.data.network.SupabaseManager.getInstance(context)
                    .likeComment(reply.id, reply.likes + 1, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {
                        @Override
                        public void onSuccess(String message) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                reply.likes += 1;
                                holder.tvLikeCount.setText(String.valueOf(reply.likes));
                                holder.btnLike.setEnabled(false);
                            });
                        }

                        @Override
                        public void onError(String error) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
            }
        });

        holder.btnMore.setOnClickListener(v -> {
            if (reply.user_id != null && reply.user_id.equals(com.fire.mangareader.data.network.SupabaseManager.getInstance(context).getCurrentUserId())) {
                new android.app.AlertDialog.Builder(context)
                    .setTitle("حذف الرد")
                    .setMessage("هل أنت متأكد من حذف هذا الرد؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        com.fire.mangareader.data.network.SupabaseManager.getInstance(context).deleteComment(reply.id, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {
                            @Override
                            public void onSuccess(String message) {
                                ((android.app.Activity) context).runOnUiThread(() -> {
                                    int pos = holder.getAdapterPosition();
                                    if (pos != RecyclerView.NO_POSITION) {
                                        replies.remove(pos);
                                        notifyItemRemoved(pos);
                                    }
                                });
                            }
                            @Override
                            public void onError(String error) {
                                ((android.app.Activity) context).runOnUiThread(() -> {
                                    android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    })
                    .setNegativeButton("إلغاء", null)
                    .show();
            } else {
                android.widget.Toast.makeText(context, "لا يمكنك التعديل على هذا الرد", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return replies != null ? replies.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername, tvDate, tvCommentText, tvLikeCount;
        public View btnLike, btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
