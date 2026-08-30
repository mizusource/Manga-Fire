with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()

content = content.replace(
    'comment.mangaUrl = obj.optString("manga_url");',
    'comment.id = obj.optString("id");\n                            comment.mangaUrl = obj.optString("manga_url");'
)

with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)
print("Updated CommentsBottomSheetDialog")
