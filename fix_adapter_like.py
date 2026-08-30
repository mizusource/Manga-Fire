with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

content = content.replace(
    'SupabaseManager.getInstance(context).likeComment(comment.id, comment.likes, new SupabaseManager.AuthCallback()',
    'SupabaseManager.getInstance(context).likeComment(comment.id, comment.likes + 1, new SupabaseManager.AuthCallback()'
)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)
