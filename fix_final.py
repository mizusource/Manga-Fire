import re
import os

# CommentsActivity.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'r') as f:
    content = f.read()
content = re.sub(r'db\.collection.*?;', '', content, flags=re.DOTALL)
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'w') as f:
    f.write(content)

# CommentsBottomSheetDialog.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()
content = re.sub(r'db\.collection.*?;', '', content, flags=re.DOTALL)
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)

# CommentAdapter.java
with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write("""package com.fire.mangareader.adapter;

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
import android.widget.ImageButton;

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
        holder.tvCommenterName.setText(comment.userName != null ? comment.userName : "User");
        holder.tvCommentText.setText(comment.text != null ? comment.text : "");
        holder.tvLikesCount.setText(String.valueOf(comment.likes));
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCommenterName, tvCommentText, tvLikesCount;
        public ImageButton btnLike, btnMore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommenterName = itemView.findViewById(R.id.tvCommenterName);
            tvCommentText = itemView.findViewById(R.id.tvCommentText);
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnMore = itemView.findViewById(R.id.btnMore);
        }
    }
}
""")
