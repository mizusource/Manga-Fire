with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

import_str = 'import com.fire.mangareader.network.SupabaseManager;\nimport android.widget.Toast;'
if 'import com.fire.mangareader.network.SupabaseManager;' not in content:
    content = content.replace('import java.util.List;', import_str + '\nimport java.util.List;')

old_like = 'holder.btnLike.setOnClickListener(v -> android.widget.Toast.makeText(context, "ميزة الإعجاب والردود قيد التطوير وسيتم تفعيلها قريباً", android.widget.Toast.LENGTH_SHORT).show());'
new_like = """holder.btnLike.setOnClickListener(v -> {
            if (comment.id != null && !comment.id.isEmpty()) {
                SupabaseManager.getInstance(context).likeComment(comment.id, comment.likes, new SupabaseManager.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        ((android.app.Activity) context).runOnUiThread(() -> {
                            comment.likes += 1;
                            holder.tvLikeCount.setText(String.valueOf(comment.likes));
                        });
                    }
                    @Override
                    public void onError(String error) {
                        ((android.app.Activity) context).runOnUiThread(() -> 
                            Toast.makeText(context, "فشل الإعجاب: " + error, Toast.LENGTH_SHORT).show()
                        );
                    }
                });
            } else {
                Toast.makeText(context, "لا يمكن الإعجاب بهذا التعليق", Toast.LENGTH_SHORT).show();
            }
        });"""

content = content.replace(old_like, new_like)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)
print("Updated CommentAdapter")
