import re

# Fix MangaDetailScreen imports
with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content = f.read()

# Replace multiple Icons imports
content = re.sub(r"import androidx\.compose\.material\.icons\.Icons\nimport androidx\.compose\.material\.icons\.Icons", "import androidx.compose.material.icons.Icons", content)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content)

# Fix MangaCommentsActivity duplicate ReportCommentDialog
with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

pattern = r"@Composable\s*fun ReportCommentDialog\(onDismissRequest: \(\) -> Unit, onReportSubmitted: \(String\) -> Unit\) \{.*"
content = re.sub(pattern, "", content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "w") as f:
    f.write(content)

