package com.fire.mangareader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.fire.mangareader.R;
import com.fire.mangareader.model.Comment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import java.util.List;
import java.util.HashMap;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    public interface OnReplyClickListener {
        void onReplyClick(Comment comment);
    }
    private OnReplyClickListener replyListener;
    public void setOnReplyClickListener(OnReplyClickListener listener) {
        this.replyListener = listener;
    }
    private Context context;
    private List<Comment> commentList;
    private String currentMangaDocId;

    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
    }
    
    public void setMangaDocId(String docId) {
        this.currentMangaDocId = docId;
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
        holder.username.setText(comment.username != null ? comment.username : comment.user_name);
        holder.commentText.setText(comment.text);
        
        holder.date.setText(com.fire.mangareader.utils.CommentUtils.getRelativeTime(comment.timestamp));
        
        holder.tvLikeCount.setText(String.valueOf(comment.likes));
        holder.tvReplyCount.setText("رد");

        // Load avatar if available
        if (comment.user_avatar != null && !comment.user_avatar.isEmpty()) {
            holder.ivAvatar.setColorFilter(null);
            com.bumptech.glide.Glide.with(context).load(comment.user_avatar).circleCrop().into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(com.fire.mangareader.R.drawable.ic_person);
            holder.ivAvatar.setColorFilter(android.graphics.Color.GRAY);
        }

        if (comment.isSpoiler || comment.is_spoiler) {
            holder.spoilerOverlay.setVisibility(View.VISIBLE);
            holder.spoilerOverlay.setOnClickListener(v -> {
                holder.spoilerOverlay.setVisibility(View.GONE);
            });
        } else {
            holder.spoilerOverlay.setVisibility(View.GONE);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean isLiked = false;
        if (user != null && comment.liked_by != null && comment.liked_by.containsKey(user.getUid())) {
            isLiked = comment.liked_by.get(user.getUid());
        }

        if (isLiked) {
            holder.ivLike.setColorFilter(android.graphics.Color.parseColor("#E91E63"));
        } else {
            holder.ivLike.setColorFilter(null);
        }

        holder.btnLike.setOnClickListener(v -> {
            if (user == null) {
                Toast.makeText(context, "يجب تسجيل الدخول للإعجاب", Toast.LENGTH_SHORT).show();
                return;
            }
            if (comment.id == null || currentMangaDocId == null) return;
            
            DocumentReference ref = FirebaseFirestore.getInstance()
                .collection("mangas").document(currentMangaDocId)
                .collection("comments").document(comment.id);
                
            if (comment.liked_by != null && comment.liked_by.containsKey(user.getUid()) && comment.liked_by.get(user.getUid())) {
                comment.likes--;
                comment.liked_by.remove(user.getUid());
                ref.update("likes", comment.likes, "liked_by." + user.getUid(), com.google.firebase.firestore.FieldValue.delete());
            } else {
                comment.likes++;
                if (comment.liked_by == null) comment.liked_by = new HashMap<>();
                comment.liked_by.put(user.getUid(), true);
                ref.update("likes", comment.likes, "liked_by." + user.getUid(), true);
            }
            notifyItemChanged(position);
        });

        holder.btnReply.setOnClickListener(v -> {
            if (replyListener != null) {
                replyListener.onReplyClick(comment);
            } else {
                Toast.makeText(context, "الرد: @" + (comment.username != null ? comment.username : comment.user_name), Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnMore.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, holder.btnMore);
            popup.getMenu().add("إبلاغ عن التعليق");
            if (user != null && user.getEmail() != null && user.getEmail().equals("mstfybdwy633@gmail.com")) {
                popup.getMenu().add("حذف التعليق");
            }
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("إبلاغ عن التعليق")) {
                    if (comment.id != null) {
                        java.util.Map<String, Object> report = new java.util.HashMap<>();
                        report.put("commentId", comment.id);
                        report.put("commentText", comment.text);
                        report.put("reportedBy", user != null ? user.getUid() : "anonymous");
                        FirebaseFirestore.getInstance().collection("reports").add(report);
                    }
                    Toast.makeText(context, "تم رفع البلاغ للإدارة لمراجعته، شكراً لك.", Toast.LENGTH_LONG).show();
                } else if (item.getTitle().equals("حذف التعليق")) {
                    if (comment.id != null && currentMangaDocId != null) {
                        FirebaseFirestore.getInstance()
                            .collection("mangas").document(currentMangaDocId)
                            .collection("comments").document(comment.id).delete();
                    }
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView username, date, commentText, tvLikeCount, tvReplyCount;
        View spoilerOverlay;
        LinearLayout btnLike, btnReply;
        ImageView ivAvatar, ivLike, btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(com.fire.mangareader.R.id.ivAvatar);
            username = itemView.findViewById(R.id.tvUsername);
            date = itemView.findViewById(R.id.tvDate);
            commentText = itemView.findViewById(R.id.tvCommentText);
            spoilerOverlay = itemView.findViewById(R.id.spoilerOverlay);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnReply = itemView.findViewById(R.id.btnReply);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvReplyCount = itemView.findViewById(R.id.tvReplyCount);
            ivLike = itemView.findViewById(R.id.ivLike);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
