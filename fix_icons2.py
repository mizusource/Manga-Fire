with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "r") as f:
    content = f.read()

# Replace all broken icons with valid ones
content = content.replace("androidx.compose.material.icons.Icons.Outlined.ChatBubbleOutline", "androidx.compose.material.icons.outlined.ChatBubbleOutline")
content = content.replace("androidx.compose.material.icons.Icons.Default.Warning", "androidx.compose.material.icons.filled.Warning")
content = content.replace("androidx.compose.material.icons.Icons.Default.Delete", "androidx.compose.material.icons.filled.Delete")
content = content.replace("androidx.compose.material.icons.Icons.AutoMirrored.Filled.Send", "androidx.compose.material.icons.automirrored.filled.Send")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/comments/MangaCommentsActivity.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content2 = f.read()

content2 = content2.replace("androidx.compose.material.icons.Icons.Outlined.Star", "androidx.compose.material.icons.outlined.Star")
content2 = content2.replace("androidx.compose.material.icons.Icons.Default.Add", "androidx.compose.material.icons.filled.Add")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content2)

