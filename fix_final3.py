import os

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

content = content.replace('new CommentsBottomSheetDialog(mangaUrl)', 'CommentsBottomSheetDialog.newInstance(mangaUrl)')

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

content = content.replace('comment.userName', 'comment.username')
# And I need to check item_comment.xml IDs
