with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

import_str = 'import android.content.SharedPreferences;'
if import_str not in content:
    content = content.replace('import android.widget.Toast;', import_str + '\nimport android.widget.Toast;')

content = content.replace(
    'holder.tvLikeCount.setText(String.valueOf(comment.likes));',
    """holder.tvLikeCount.setText(String.valueOf(comment.likes));
                            
                            SharedPreferences prefs = context.getApplicationContext().getSharedPreferences("supabase_prefs", Context.MODE_PRIVATE);
                            String currentUserName = prefs.getString("user_name", "مستخدم");
                            if(comment.user_id != null && !comment.user_id.isEmpty()) {
                                SupabaseManager.getInstance(context).sendNotification(
                                    comment.user_id,
                                    currentUserName,
                                    "أعجب بتعليقك",
                                    comment.mangaUrl,
                                    "like"
                                );
                            }"""
)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)
