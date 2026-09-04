import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r", encoding="utf-8") as f:
    content = f.read()

# Fix the escaped quotes that got messed up
bad_line = 'String unescaped = jsonResult.replaceAll("^\\"|\\"$", "").replace("\\\\\\"", "\\"").replace("\\\\\\\\", "\\\\");'
good_line = 'String unescaped = jsonResult.replaceAll("^\\"|\\"$", "").replace("\\\\\\"", "\\"").replace("\\\\\\\\", "\\\\");'

content = content.replace('String unescaped = jsonResult.replaceAll("^\\"|\\"$", "").replace("\\"", "\\"").replace("\\", "\\");', 'String unescaped = jsonResult.replaceAll("^\\"|\\"$", "").replace("\\\\\\"", "\\"").replace("\\\\\\\\", "\\\\");')

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w", encoding="utf-8") as f:
    f.write(content)
print("patched string error")
