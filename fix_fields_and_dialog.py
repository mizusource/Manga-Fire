import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

content = content.replace("private String chapterTitle;", "private String chapterTitle;\n    private String mangaTitle;\n    private String mangaCover;")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(content)

