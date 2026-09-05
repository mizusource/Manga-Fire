with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

# Fix icon references in MangaCommentsActivity
content = content.replace("androidx.compose.material.icons.outlined.ChatBubbleOutline", "androidx.compose.material.icons.Icons.Outlined.ChatBubbleOutline")
content = content.replace("androidx.compose.material.icons.filled.Warning", "androidx.compose.material.icons.Icons.Default.Warning")
content = content.replace("androidx.compose.material.icons.filled.Delete", "androidx.compose.material.icons.Icons.Default.Delete")
content = content.replace("androidx.compose.material.icons.automirrored.filled.Send", "androidx.compose.material.icons.Icons.AutoMirrored.Filled.Send")

# Remove duplicate ReportCommentDialog if it exists
import re
count = content.count("fun ReportCommentDialog(")
if count > 1:
    # Just remove the last occurrence which is the one we appended
    pattern = r"@Composable\s*fun ReportCommentDialog\(onDismissRequest: \(\) -> Unit, onReportSubmitted: \(String\) -> Unit\) \{.*"
    content = re.sub(pattern, "", content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content2 = f.read()

content2 = content2.replace("androidx.compose.material.icons.outlined.Star", "androidx.compose.material.icons.Icons.Outlined.Star")
content2 = content2.replace("androidx.compose.material.icons.filled.Add", "androidx.compose.material.icons.Icons.Default.Add")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content2)

