package com.fire.mangareader.presentation.ui.screens.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.local.AppDatabase
import com.fire.mangareader.data.local.entity.FavoriteManga
import com.fire.mangareader.data.network.MangaScraper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val favoriteDao = db.favoriteDao()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _mangaTitle = MutableStateFlow<String>("")
    val mangaTitle: StateFlow<String> = _mangaTitle
    
    private val _description = MutableStateFlow<String>("")
    val description: StateFlow<String> = _description

    private val _status = MutableStateFlow<String>("")
    val status: StateFlow<String> = _status

    private val _chapters = MutableStateFlow<List<com.fire.mangareader.domain.model.Chapter>>(emptyList())
    val chapters: StateFlow<List<com.fire.mangareader.domain.model.Chapter>> = _chapters

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun saveToHistory(mangaId: String, chapter: com.fire.mangareader.domain.model.Chapter) {
        val coverUrl = "https://mangalik.net/uploads/manga/cover/$mangaId/cover_250x350.jpg"
        viewModelScope.launch {
            db.recentDao().insertRecent(
                com.fire.mangareader.data.local.entity.RecentManga(
                    id = mangaId,
                    title = _mangaTitle.value.ifEmpty { "Unknown" },
                    coverUrl = coverUrl,
                    lastReadChapterId = chapter.url?.trimEnd('/')?.split("/")?.lastOrNull() ?: "",
                    lastReadChapterName = chapter.title ?: "Unknown"
                )
            )
        }
    }

    fun checkFavoriteStatus(mangaId: String) {
        viewModelScope.launch {
            favoriteDao.isFavorite(mangaId).collect {
                _isFavorite.value = it
            }
        }
    }

    fun toggleFavorite(mangaId: String) {
        val coverUrl = "https://mangalik.net/uploads/manga/cover/$mangaId/cover_250x350.jpg"
        viewModelScope.launch {
            if (_isFavorite.value) {
                favoriteDao.deleteFavorite(mangaId)
            } else {
                favoriteDao.insertFavorite(
                    FavoriteManga(
                        id = mangaId,
                        title = _mangaTitle.value.ifEmpty { "Unknown" },
                        coverUrl = coverUrl,
                        latestChapter = _chapters.value.firstOrNull()?.title ?: "",
                        rating = ""
                    )
                )
            }
        }
    }

    fun fetchDetails(mangaId: String) {
        _isLoading.value = true
        _error.value = null
        val mangaUrl = MangaScraper.BASE_URL + "manga/" + mangaId
        _mangaTitle.value = mangaId.replace("-", " ").capitalize()
        MangaScraper.fetchMangaDetails(mangaUrl, object : MangaScraper.MangaDetailsCallback {
            override fun onSuccess(
                description: String?,
                status: String?,
                chapters: MutableList<com.fire.mangareader.domain.model.Chapter>?
            ) {
                _description.value = description ?: "لا يوجد وصف"
                _status.value = status ?: "غير معروف"
                _chapters.value = chapters?.reversed() ?: emptyList()
                _isLoading.value = false
            }

            override fun onError(errorMessage: String?) {
                _error.value = errorMessage ?: "حدث خطأ غير معروف"
                _isLoading.value = false
            }
        })
    }
}
