import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

decode_func = """
    private fun decodeUrl(id: String): String {
        if (id.startsWith("http://") || id.startsWith("https://")) return id
        return try {
            String(android.util.Base64.decode(id, android.util.Base64.URL_SAFE))
        } catch (e: Exception) {
            id
        }
    }
"""

if "decodeUrl" not in content:
    content = content.replace("class MainComposeActivity : ComponentActivity() {", "class MainComposeActivity : ComponentActivity() {\n" + decode_func)

content = content.replace('putExtra("mangaUrl", mangaId)', 'putExtra("mangaUrl", decodeUrl(mangaId))')
content = content.replace('putExtra("chapterUrl", chapterId)', 'putExtra("chapterUrl", decodeUrl(chapterId))')

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
