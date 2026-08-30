with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()

content = content.replace(
    'comment.username = obj.optString("username");',
    'comment.username = obj.optString("username");\n                            comment.user_id = obj.optString("user_id");'
)

with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)
