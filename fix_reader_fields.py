import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

content = content.replace("private String chapterUrl, mangaUrl, chapterTitle;", "private String chapterUrl, mangaUrl, chapterTitle, mangaTitle, mangaCover;")
content = content.replace("String mangaTitle = getIntent().getStringExtra(\"mangaTitle\");", "mangaTitle = getIntent().getStringExtra(\"mangaTitle\");")
content = content.replace("String mangaCover = getIntent().getStringExtra(\"mangaCover\");", "mangaCover = getIntent().getStringExtra(\"mangaCover\");")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(content)

