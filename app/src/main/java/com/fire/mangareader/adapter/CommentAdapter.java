package com.fire.mangareader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.model.Comment;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context context;
    private List<Comment> commentList;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.username.setText(comment.username);
        holder.commentText.setText(comment.text);
        
        // تبسيط عرض التاريخ (يمكنك تحسينها لاحقاً لحساب الوقت الفعلي)
        holder.date.setText("جديد"); 

        // برمجة ميزة "الحرق" (Spoiler)
        if (comment.isSpoiler) {
            holder.spoilerOverlay.setVisibility(View.VISIBLE);
            
            // عند الضغط على طبقة الحرق، نقوم بإخفائها ليظهر النص
            holder.spoilerOverlay.setOnClickListener(v -> {
                holder.spoilerOverlay.setVisibility(View.GONE);
            });
        } else {
            holder.spoilerOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, date, commentText;
        View spoilerOverlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUsername);
            date = itemView.findViewById(R.id.tvDate);
            commentText = itemView.findViewById(R.id.tvCommentText);
            spoilerOverlay = itemView.findViewById(R.id.spoilerOverlay);
        }
    }
}
