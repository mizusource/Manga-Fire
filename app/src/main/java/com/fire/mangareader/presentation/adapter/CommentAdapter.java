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
import android.content.Intent;
import com.fire.mangareader.presentation.activity.RepliesActivity;
import com.fire.mangareader.data.network.SupabaseManager;
import android.content.SharedPreferences;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.text.ParseException;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private final Context context;
    private final List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvUsername.setText(comment.username != null ? comment.username : "User");
        holder.tvCommentText.setText(comment.text != null ? comment.text : "");

        // Parse time and calculate time ago
        if (comment.created_at != null && !comment.created_at.isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdf.parse(comment.created_at);
                if (date != null) {
                    holder.tvDate.setText(com.fire.mangareader.util.TimeUtils.getTimeAgo(date.getTime()));
                }
            } catch (ParseException e) {
                holder.tvDate.setText("");
            }
        } else {
            holder.tvDate.setText("");
        }

        holder.tvLikeCount.setText(String.valueOf(comment.likes));

        
        holder.btnReply.setOnClickListener(v -> {
                        Intent intent = new Intent(context, RepliesActivity.class);
            intent.putExtra("mangaUrl", comment.mangaUrl);
            intent.putExtra("parentId", comment.id);
            intent.putExtra("parentUserId", comment.user_id);
            context.startActivity(intent);
        });
        
        holder.btnMore.setOnClickListener(v -> {
            if (comment.user_id != null && comment.user_id.equals(com.fire.mangareader.data.network.SupabaseManager.getInstance(context).getCurrentUserId())) {
                // User can delete their own comment
                new android.app.AlertDialog.Builder(context)
                    .setTitle("حذف التعليق")
                    .setMessage("هل أنت متأكد من حذف هذا التعليق؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        com.fire.mangareader.data.network.SupabaseManager.getInstance(context).deleteComment(comment.id, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {
                            @Override
                            public void onSuccess(String message) {
                                ((android.app.Activity) context).runOnUiThread(() -> {
                                    int pos = holder.getAdapterPosition();
                                    if (pos != RecyclerView.NO_POSITION) {
                                        comments.remove(pos);
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
                android.widget.Toast.makeText(context, "لا يمكنك التعديل على هذا التعليق", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnLike.setOnClickListener(v -> {
            if (comment.id != null) {
                com.fire.mangareader.data.network.SupabaseManager.getInstance(context)
                    .likeComment(comment.id, comment.likes + 1, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {
                        @Override
                        public void onSuccess(String message) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                comment.likes += 1;
                                holder.tvLikeCount.setText(String.valueOf(comment.likes));
                                holder.btnLike.setEnabled(false);
                            });
                            // Send notification
                            if (comment.user_id != null && !comment.user_id.isEmpty()) {
                                String senderName = context.getSharedPreferences("supabase_prefs", android.content.Context.MODE_PRIVATE).getString("username", "مستخدم");
                                com.fire.mangareader.data.network.SupabaseManager.getInstance(context)
                                    .sendNotification(comment.user_id, senderName, "أعجب بتعليقك", comment.mangaUrl, null);
                            }
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
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername, tvDate, tvCommentText, tvLikeCount;
        public View btnLike, btnMore, btnReply;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnMore = itemView.findViewById(R.id.btnMore);
            btnReply = itemView.findViewById(R.id.btnReply);
        }
    }
}
