import re
with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

content = content.replace(
    '''SupabaseManager.getInstance(context).sendNotification(
                                    comment.user_id,
                                    currentUserName,
                                    "أعجب بتعليقك",
                                    comment.mangaUrl,
                                    "like"
                                );''',
    '''SupabaseManager.getInstance(context).sendNotification(
                                    comment.user_id,
                                    currentUserName,
                                    "أعجب بتعليقك",
                                    comment.mangaUrl,
                                    null
                                );'''
)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)
