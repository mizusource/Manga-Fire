with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

content = content.replace("com.fire.mangareader.presentation.ui.comments.MangaCommentsActivity.class", "CommentsActivity.class")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)
