import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsBottomSheetDialog.java", "r") as f:
    text = f.read()

text = text.replace("R.id.etCommentInput", "R.id.etCommentInput") # Already correct
text = text.replace("android.widget.ImageView btnSendComment;", "android.widget.ImageButton btnSendComment;")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsBottomSheetDialog.java", "w") as f:
    f.write(text)
