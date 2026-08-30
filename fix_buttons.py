with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

btn_listeners = """
        holder.btnReply.setOnClickListener(v -> {
            android.widget.Toast.makeText(context, "ميزة الردود ستتوفر قريباً", android.widget.Toast.LENGTH_SHORT).show();
        });
        
        holder.btnMore.setOnClickListener(v -> {
            if (comment.user_id != null && comment.user_id.equals(com.fire.mangareader.network.SupabaseManager.getInstance(context).getCurrentUserId())) {
                // User can delete their own comment
                new android.app.AlertDialog.Builder(context)
                    .setTitle("حذف التعليق")
                    .setMessage("هل أنت متأكد من حذف هذا التعليق؟")
                    .setPositiveButton("حذف", (dialog, which) -> {
                        com.fire.mangareader.network.SupabaseManager.getInstance(context).deleteComment(comment.id, new com.fire.mangareader.network.SupabaseManager.AuthCallback() {
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
"""

if 'holder.btnReply.setOnClickListener' not in content:
    content = content.replace('public View btnLike, btnMore;', 'public View btnLike, btnMore, btnReply;')
    content = content.replace('btnMore = itemView.findViewById(R.id.btnMore);', 'btnMore = itemView.findViewById(R.id.btnMore);\n            btnReply = itemView.findViewById(R.id.btnReply);')
    content = content.replace('holder.btnLike.setOnClickListener', btn_listeners + '\n        holder.btnLike.setOnClickListener')
    with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
        f.write(content)
