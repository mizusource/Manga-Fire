filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/library/LibraryViewModel.kt'
with open(filepath, 'r') as f:
    content = f.read()

if 'import kotlinx.coroutines.launch' not in content:
    content = content.replace('import kotlinx.coroutines.flow.stateIn', 'import kotlinx.coroutines.flow.stateIn\nimport kotlinx.coroutines.launch')

# Fix compile issues related to type mismatch and missing methods by replacing all
content = """package com.fire.mangareader.presentation.ui.screens.library

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.local.AppDatabase
import com.fire.mangareader.presentation.ui.screens.home.UIManga
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val favoriteDao = db.favoriteDao()
    private val recentDao = db.recentDao()

    val favorites = favoriteDao.getAllFavorites().map { list ->
        list.map { manga ->
            UIManga(
                id = manga.id,
                title = manga.title,
                coverUrl = manga.coverUrl,
                latestChapter = manga.latestChapter,
                rating = manga.rating
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList<UIManga>())

    val history = recentDao.getRecentHistory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
}
"""

with open(filepath, 'w') as f:
    f.write(content)
