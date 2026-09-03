with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content = f.read()

if "import androidx.compose.runtime.remember" not in content:
    content = content.replace("import androidx.compose.runtime.Composable", "import androidx.compose.runtime.Composable\nimport androidx.compose.runtime.remember\nimport androidx.compose.runtime.rememberCoroutineScope")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(content)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content2 = f.read()

if "import androidx.compose.material.icons.filled.PlayArrow" not in content2:
    content2 = content2.replace("import androidx.compose.material.icons.filled.Person", "import androidx.compose.material.icons.filled.Person\nimport androidx.compose.material.icons.filled.PlayArrow")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.write(content2)

