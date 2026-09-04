with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
skip = False
for line in lines:
    if "if (showSettings) {" in line:
        pass
    if line.strip() == "}":
        pass
    new_lines.append(line)

# Let's just fix it carefully
import re
with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "r") as f:
    text = f.read()

text = re.sub(r'\}\s*\}\s*\}\s*@Composable', '} } @Composable', text)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "w") as f:
    f.write(text)
