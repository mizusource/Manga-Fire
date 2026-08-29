import re
import os

# MainActivity.java
with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

content = re.sub(r'Object user = null;.*?updateNavHeader\(\);', 'Object user = null; updateNavHeader();', content, flags=re.DOTALL)
content = re.sub(r'private void updateNavHeader\(\) \{.*?\}', 'private void updateNavHeader() {}', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)

# AdminDashboardActivity.java
with open('app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java', 'r') as f:
    content = f.read()
content = re.sub(r'if \(currentUser == null.*?\}', 'if (true) { finish(); return; }', content, flags=re.DOTALL)
content = re.sub(r'Object ref = null;.*?\}\);', 'Object ref = null;', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java', 'w') as f:
    f.write(content)

# CommentsActivity.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'r') as f:
    content = f.read()
content = content.replace('adapter.setMangaDocId(mangaUrl.replaceAll("[^a-zA-Z0-9]", "_"));', '')
content = re.sub(r'adapter\.setOnReplyClickListener.*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'w') as f:
    f.write(content)

# CommentsBottomSheetDialog.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()
content = content.replace('adapter.setMangaDocId(mangaUrl.replaceAll("[^a-zA-Z0-9]", "_"));', '')
content = re.sub(r'adapter\.setOnReplyClickListener.*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)

# CommentAdapter.java
with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()
content = content.replace('comment.userName', 'comment.authorName')
content = content.replace('tvCommenterName', 'tvAuthorName')
content = content.replace('tvLikesCount', 'tvLikes')
content = content.replace('btnDislike', 'btnLike') # reuse
with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)
