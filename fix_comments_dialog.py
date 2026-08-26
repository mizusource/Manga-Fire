import re

with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()

content = content.replace('comment.setId(doc.getId());', 'comment.id = doc.getId();')
content = content.replace('newComment.setUserName(userName);', 'newComment.user_name = userName;\n        newComment.username = userName;')
content = content.replace('newComment.setCommentText(text);', 'newComment.text = text;')
content = content.replace('newComment.setTimestamp(System.currentTimeMillis());', 'newComment.timestamp = System.currentTimeMillis();')

with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)
