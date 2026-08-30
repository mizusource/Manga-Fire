with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

import_str = 'import android.content.Intent;\nimport com.fire.mangareader.activity.RepliesActivity;\n'
if 'RepliesActivity' not in content:
    content = content.replace('import com.fire.mangareader.network.SupabaseManager;', import_str + 'import com.fire.mangareader.network.SupabaseManager;')
    
    reply_code = """
        holder.btnReply.setOnClickListener(v -> {
            Intent intent = new Intent(context, RepliesActivity.class);
            intent.putExtra("mangaUrl", comment.mangaUrl);
            intent.putExtra("parentId", comment.id);
            intent.putExtra("parentUserId", comment.user_id);
            context.startActivity(intent);
        });
"""
    # Replace the toast with the intent
    content = content.replace('android.widget.Toast.makeText(context, "ميزة الردود ستتوفر قريباً", android.widget.Toast.LENGTH_SHORT).show();', 
                              '            Intent intent = new Intent(context, RepliesActivity.class);\n            intent.putExtra("mangaUrl", comment.mangaUrl);\n            intent.putExtra("parentId", comment.id);\n            intent.putExtra("parentUserId", comment.user_id);\n            context.startActivity(intent);')
    
    with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
        f.write(content)
