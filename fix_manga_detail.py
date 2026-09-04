import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    text = f.read()

bad_pattern = """listOf("أكشن", "خيال", "شونين", "مغامرة").forEach { genre ->
                androidx.compose.material3.SuggestionChip(
                    onClick = { },
                    label = { Text(genre) }
                )
            }"""

good_pattern = """items(listOf("أكشن", "خيال", "شونين", "مغامرة")) { genre ->
                androidx.compose.material3.SuggestionChip(
                    onClick = { },
                    label = { Text(genre) }
                )
            }"""

text = text.replace(bad_pattern, good_pattern)

# also need to import items?
text = text.replace("import androidx.compose.foundation.lazy.LazyColumn", "import androidx.compose.foundation.lazy.LazyColumn\nimport androidx.compose.foundation.lazy.items")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(text)
