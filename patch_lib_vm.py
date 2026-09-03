filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/library/LibraryViewModel.kt'
with open(filepath, 'r') as f:
    content = f.read()

new_methods = """
    fun removeFavorite(mangaId: String) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(mangaId)
        }
    }

    fun removeHistory(mangaId: String) {
        viewModelScope.launch {
            recentDao.deleteRecent(mangaId)
        }
    }
}"""

content = content.replace('}', new_methods)

# Fix duplicate brackets if they exist
content = content.replace('}\n\n}', '}')

with open(filepath, 'w') as f:
    f.write(content)
