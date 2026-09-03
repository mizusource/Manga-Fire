with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/CommentsViewModel.kt", "r") as f:
    content = f.read()

content = content.replace("repository = CommentsRepository(database.commentDao())", "repository = CommentsRepository(database.commentDao(), application)")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/CommentsViewModel.kt", "w") as f:
    f.write(content)
