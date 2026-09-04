import re

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/CommentAdapter.java", "r") as f:
    text = f.read()

# Replace setOnClickListener for btnLike
pattern = r'holder\.btnLike\.setOnClickListener\(v -> \{.*?(if \(comment\.id != null\).*?)\}\);'

replacement = """
        holder.btnLike.setOnClickListener(v -> {
            if (comment.id != null) {
                android.widget.ViewFlipper flipper = holder.btnLike.findViewById(R.id.actionFlipper);
                if (flipper != null) flipper.setDisplayedChild(1); // Show loading
                
                com.fire.mangareader.data.network.SupabaseManager.getInstance(context)
                    .likeComment(comment.id, comment.likes + 1, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {
                        @Override
                        public void onSuccess(String message) {
                            ((android.app.Activity) context).runOnUiThread(() -> {
                                comment.likes += 1;
                                holder.tvLikeCount.setText(String.valueOf(comment.likes));
                                if (flipper != null) flipper.setDisplayedChild(2); // Show active
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
                                if (flipper != null) flipper.setDisplayedChild(0); // Show normal
                                android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
            }
        });
"""

text = re.sub(r'holder\.btnLike\.setOnClickListener\(v -> \{.*?\}\}\);', replacement.strip(), text, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/CommentAdapter.java", "w") as f:
    f.write(text)
