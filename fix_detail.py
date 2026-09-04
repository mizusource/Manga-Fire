import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    text = f.read()

# Replace FlowRow with LazyRow
text = text.replace("androidx.compose.foundation.layout.FlowRow(", "androidx.compose.foundation.lazy.LazyRow(")

# Add sp import
text = text.replace("import androidx.compose.ui.unit.dp", "import androidx.compose.ui.unit.dp\nimport androidx.compose.ui.unit.sp")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(text)
