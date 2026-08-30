import re

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

clean_bind = """
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvUsername.setText(comment.username != null ? comment.username : "User");
        holder.tvCommentText.setText(comment.text != null ? comment.text : "");
        holder.tvLikeCount.setText(String.valueOf(comment.likes));

        holder.btnLike.setOnClickListener(v -> {
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
                            // Send notification
                            if (comment.user_id != null && !comment.user_id.isEmpty()) {
                                String senderName = context.getSharedPreferences("supabase_prefs", android.content.Context.MODE_PRIVATE).getString("username", "مستخدم");
                                com.fire.mangareader.network.SupabaseManager.getInstance(context)
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
"""

new_content = re.sub(r'@Override\s*public void onBindViewHolder\(\@NonNull ViewHolder holder, int position\).*?@Override\s*public int getItemCount', clean_bind + '\n    @Override\n    public int getItemCount', content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(new_content)
