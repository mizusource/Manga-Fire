with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    text = f.read()

bad_string = 'String unescaped = jsonResult.replaceAll("^\\"|\\"$", "").replace("\\\\"", "\\"").replace("\\\\", "\\");'
good_string = 'String unescaped = jsonResult.replaceAll("^\\"|\\"$", "").replace("\\\\\\\"", "\\\"").replace("\\\\\\\\", "\\\\");'

text = text.replace(bad_string, good_string)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(text)
