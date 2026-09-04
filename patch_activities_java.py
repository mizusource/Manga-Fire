import re

# CommentsActivity
with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsActivity.java", "r") as f:
    text = f.read()

text = text.replace("R.id.etComment", "R.id.etCommentInput")
# If it uses TextInputEditText, we might need to change it to EditText or AppCompatEditText
text = text.replace("TextInputEditText etComment;", "android.widget.EditText etComment;")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsActivity.java", "w") as f:
    f.write(text)

# RepliesActivity
with open("app/src/main/java/com/fire/mangareader/presentation/activity/RepliesActivity.java", "r") as f:
    text = f.read()

text = text.replace("R.id.etReply", "R.id.etCommentInput")
text = text.replace("R.id.btnSend", "R.id.btnSendComment")
text = text.replace("TextInputEditText etReply;", "android.widget.EditText etReply;")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/RepliesActivity.java", "w") as f:
    f.write(text)
