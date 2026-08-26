# Fix AdminDashboardActivity
with open("app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java", "r") as f:
    content = f.read()
content = content.replace("super.onCreate(Bundle.valueOf(1)); // Dummy bundle to enforce edge-to-edge if needed\n", "")
with open("app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java", "w") as f:
    f.write(content)

# Fix CommentsActivity
with open("app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java", "r") as f:
    content = f.read()
content = content.replace("private CheckBox cbSpoiler;", "")
content = content.replace("cbSpoiler = findViewById(R.id.cbSpoiler);", "")
content = content.replace("boolean isSpoiler = cbSpoiler.isChecked();", "boolean isSpoiler = false;")
with open("app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java", "w") as f:
    f.write(content)
