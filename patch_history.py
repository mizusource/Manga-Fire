import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/DetailViewModel.kt'
with open(filepath, 'r') as f:
    content = f.read()

new_method = """    fun saveToHistory(chapter: com.fire.mangareader.domain.model.Chapter) {
        val currentManga = _mangaDetails.value ?: return
        viewModelScope.launch {
            db.recentDao().insertRecent(
                com.fire.mangareader.data.local.entity.RecentManga(
                    id = currentManga.url?.trimEnd('/')?.split("/")?.lastOrNull() ?: "",
                    title = currentManga.title ?: "Unknown",
                    coverUrl = currentManga.coverUrl ?: "",
                    lastReadChapterId = chapter.url?.trimEnd('/')?.split("/")?.lastOrNull() ?: "",
                    lastReadChapterName = chapter.title ?: "Unknown"
                )
            )
        }
    }
"""

content = content.replace('fun checkFavoriteStatus', new_method + '\n    fun checkFavoriteStatus')

with open(filepath, 'w') as f:
    f.write(content)
