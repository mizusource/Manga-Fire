with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

import re
# Find the line that starts with "String cleanHtml = html.replaceAll"
content = re.sub(r'String cleanHtml = html\.replaceAll.*?;', 'String cleanHtml = html.replaceAll("^\\"|\\"$", "").replace("\\\\u003C", "<").replace("\\\\u003E", ">").replace("\\\\\\"", "\\"").replace("\\\\n", " ").replace("\\\\t", " ").replace("\\\\\\\\", "");', content)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
