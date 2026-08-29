import re

with open('app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java', 'r') as f:
    content = f.read()

# Make it a class variable
if 'private String chapterTitle;' not in content:
    content = content.replace('private String mangaUrl;', 'private String mangaUrl;\n    private String chapterTitle;')

content = content.replace('String chapterTitle = getIntent().getStringExtra("chapterTitle");', 'chapterTitle = getIntent().getStringExtra("chapterTitle");')

with open('app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java', 'w') as f:
    f.write(content)
