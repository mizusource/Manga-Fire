import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/DetailViewModel.kt'
with open(filepath, 'r') as f:
    content = f.read()

new_imports = """import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.fire.mangareader.data.local.AppDatabase
import com.fire.mangareader.data.local.entity.FavoriteManga
"""

content = content.replace('import androidx.lifecycle.ViewModel', new_imports)

content = content.replace('class DetailViewModel : ViewModel() {', 'class DetailViewModel(application: Application) : AndroidViewModel(application) {\n    private val db = AppDatabase.getDatabase(application)\n    private val favoriteDao = db.favoriteDao()\n\n    private val _isFavorite = MutableStateFlow(false)\n    val isFavorite: StateFlow<Boolean> = _isFavorite\n\n    fun checkFavoriteStatus(mangaId: String) {\n        viewModelScope.launch {\n            favoriteDao.isFavorite(mangaId).collect {\n                _isFavorite.value = it\n            }\n        }\n    }\n\n    fun toggleFavorite(mangaId: String) {\n        val currentManga = _mangaDetails.value ?: return\n        viewModelScope.launch {\n            if (_isFavorite.value) {\n                favoriteDao.deleteFavorite(mangaId)\n            } else {\n                favoriteDao.insertFavorite(\n                    FavoriteManga(\n                        id = mangaId,\n                        title = currentManga.title ?: "Unknown",\n                        coverUrl = currentManga.coverUrl ?: "",\n                        latestChapter = currentManga.latestChapter ?: "",\n                        rating = currentManga.rating ?: ""\n                    )\n                )\n            }\n        }\n    }')

with open(filepath, 'w') as f:
    f.write(content)
