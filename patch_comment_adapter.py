with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    adapter = f.read()

new_like = """holder.btnLike.setOnClickListener(v -> {
            if (comment.id != null) {
                com.fire.mangareader.network.SupabaseManager.getInstance(context)
                    .likeComment(comment.id, comment.likes + 1, new com.fire.mangareader.network.SupabaseManager.AuthCallback() {
                        @Override
                        public void onSuccess(String message) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                comment.likes += 1;
                                holder.tvLikeCount.setText(String.valueOf(comment.likes));
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
                
                // Also send a notification if user_id is available
                if (comment.user_id != null && !comment.user_id.isEmpty()) {
                    String senderName = context.getSharedPreferences("supabase_prefs", android.content.Context.MODE_PRIVATE).getString("username", "مستخدم");
                    com.fire.mangareader.network.SupabaseManager.getInstance(context)
                        .sendNotification(comment.user_id, senderName, "أعجب بتعليقك", comment.mangaUrl, null);
                }
            }
        });"""

import re
adapter = re.sub(r'holder\.btnLike\.setOnClickListener\(v -> \{.*?\}\);', new_like, adapter, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(adapter)
print("Patched CommentAdapter with direct Supabase calls")
