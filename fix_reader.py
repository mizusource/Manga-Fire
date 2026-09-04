import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "r") as f:
    text = f.read()

# Fix FontWeight missing import
if "import androidx.compose.ui.text.font.FontWeight" not in text:
    text = text.replace("import androidx.compose.ui.text.style.TextOverflow", "import androidx.compose.ui.text.style.TextOverflow\nimport androidx.compose.ui.text.font.FontWeight")

# Fix pagerState missing
pager_pattern = r'var filterMode by remember \{ mutableStateOf\(0\) \}'
text = re.sub(pager_pattern, 'var filterMode by remember { mutableStateOf(0) }\n    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })\n    val coroutineScope = rememberCoroutineScope()', text)


with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/reader/ChapterReaderScreen.kt", "w") as f:
    f.write(text)
