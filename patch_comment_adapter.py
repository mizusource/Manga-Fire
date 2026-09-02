import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/adapter/CommentAdapter.java'
with open(filepath, 'r') as f:
    content = f.read()

# Add blur effect imports if needed, though we can just hide text and show overlay
if "import android.graphics.RenderEffect" not in content:
    content = content.replace('import android.widget.TextView;', 'import android.widget.TextView;\nimport android.os.Build;\nimport android.graphics.RenderEffect;\nimport android.graphics.Shader;')

# Update onBindViewHolder
old_bind = """        holder.tvUsername.setText(comment.username != null ? comment.username : "User");
        holder.tvCommentText.setText(comment.text != null ? comment.text : "");"""

new_bind = """        holder.tvUsername.setText(comment.username != null ? comment.username : "User");
        holder.tvCommentText.setText(comment.text != null ? comment.text : "");
        
        TextView spoilerOverlay = holder.itemView.findViewById(R.id.spoilerOverlay);
        if (comment.isSpoiler || comment.is_spoiler) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                holder.tvCommentText.setRenderEffect(RenderEffect.createBlurEffect(15f, 15f, Shader.TileMode.CLAMP));
                if (spoilerOverlay != null) spoilerOverlay.setVisibility(View.VISIBLE);
            } else {
                holder.tvCommentText.setText("██████████████████");
                if (spoilerOverlay != null) spoilerOverlay.setVisibility(View.VISIBLE);
            }
            
            holder.itemView.setOnClickListener(v -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    holder.tvCommentText.setRenderEffect(null);
                } else {
                    holder.tvCommentText.setText(comment.text != null ? comment.text : "");
                }
                if (spoilerOverlay != null) spoilerOverlay.setVisibility(View.GONE);
                comment.isSpoiler = false;
                comment.is_spoiler = false;
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                holder.tvCommentText.setRenderEffect(null);
            }
            holder.tvCommentText.setText(comment.text != null ? comment.text : "");
            if (spoilerOverlay != null) spoilerOverlay.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
        }"""

content = content.replace(old_bind, new_bind)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched CommentAdapter.java")
