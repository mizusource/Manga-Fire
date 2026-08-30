package com.fire.mangareader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.model.Comment;
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
        holder.tvLikeCount.setText(String.valueOf(comment.likes));
        holder.btnLike.setOnClickListener(v -> android.widget.Toast.makeText(context, "ميزة الإعجاب والردود قيد التطوير وسيتم تفعيلها قريباً", android.widget.Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUsername, tvCommentText, tvLikeCount;
        public View btnLike, btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
