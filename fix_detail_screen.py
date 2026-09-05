with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content = f.read()

content = content.replace("androidx.compose.material.icons.Icons.Default.StarBorder", "androidx.compose.material.icons.outlined.Star")
content = content.replace("androidx.compose.material.icons.Icons.Default.Add", "androidx.compose.material.icons.filled.Add")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content)
