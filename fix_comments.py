with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()

content = content.replace('private ImageButton btnSendComment;', 'private android.widget.ImageView btnSendComment;')

with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)
