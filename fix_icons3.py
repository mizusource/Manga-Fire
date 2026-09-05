with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

# Make it clean by using imported Icons
content = content.replace("androidx.compose.material.icons.outlined.ChatBubbleOutline", "Icons.Outlined.ChatBubbleOutline")
content = content.replace("androidx.compose.material.icons.filled.Warning", "Icons.Default.Warning")
content = content.replace("androidx.compose.material.icons.filled.Delete", "Icons.Default.Delete")
content = content.replace("androidx.compose.material.icons.automirrored.filled.Send", "Icons.AutoMirrored.Filled.Send")
content = content.replace("androidx.compose.material.icons.Icons.Default.KeyboardArrowUp", "Icons.Default.KeyboardArrowUp")
content = content.replace("androidx.compose.material.icons.Icons.Default.KeyboardArrowDown", "Icons.Default.KeyboardArrowDown")
content = content.replace("androidx.compose.material.icons.Icons.Default.MoreVert", "Icons.Default.MoreVert")

imports = """import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.automirrored.filled.*
"""
if "import androidx.compose.material.icons.filled.*" not in content:
    content = content.replace("import androidx.compose.material.icons.Icons", imports)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content2 = f.read()

content2 = content2.replace("androidx.compose.material.icons.outlined.Star", "Icons.Outlined.Star")
content2 = content2.replace("androidx.compose.material.icons.filled.Add", "Icons.Default.Add")
content2 = content2.replace("androidx.compose.material.icons.Icons.Default.Star", "Icons.Default.Star")

if "import androidx.compose.material.icons.outlined.*" not in content2:
    content2 = content2.replace("import androidx.compose.material.icons.filled.Star", "import androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.*\nimport androidx.compose.material.icons.outlined.*")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content2)

