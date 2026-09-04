import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsActivity.java", "r") as f:
    text = f.read()

pattern = r'(etComment = findViewById\(R\.id\.etCommentInput\);)'
replacement = r'\1\n        android.view.View cbSpoiler = findViewById(R.id.cbSpoiler);\n        if (cbSpoiler != null) cbSpoiler.setVisibility(android.view.View.VISIBLE);'
new_text = re.sub(pattern, replacement, text)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CommentsActivity.java", "w") as f:
    f.write(new_text)
