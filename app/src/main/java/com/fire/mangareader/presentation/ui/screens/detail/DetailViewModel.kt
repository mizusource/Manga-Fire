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
    
    private var currentCoverUrl: String = ""

    fun saveToHistory(mangaId: String, chapter: com.fire.mangareader.domain.model.Chapter) {
        viewModelScope.launch {
            db.recentDao().insertRecent(
                com.fire.mangareader.data.local.entity.RecentManga(
                    id = mangaId,
                    title = _mangaTitle.value.ifEmpty { "Unknown" },
                    coverUrl = currentCoverUrl,
                    lastReadChapterId = android.util.Base64.encodeToString(chapter.url?.toByteArray() ?: ByteArray(0), android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP or android.util.Base64.NO_PADDING),
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
        viewModelScope.launch {
            if (_isFavorite.value) {
                favoriteDao.deleteFavorite(mangaId)
            } else {
                favoriteDao.insertFavorite(
                    FavoriteManga(
                        id = mangaId,
                        title = _mangaTitle.value.ifEmpty { "Unknown" },
                        coverUrl = currentCoverUrl,
                        latestChapter = _chapters.value.firstOrNull()?.title ?: "",
                        rating = ""
                    )
                )
            }
        }
    }
    
    private fun decodeIdToUrl(id: String): String {
        return try {
            val decoded = String(android.util.Base64.decode(id, android.util.Base64.URL_SAFE))
            if (decoded.startsWith("http")) decoded else MangaScraper.BASE_URL + "manga/" + id
        } catch (e: Exception) {
            MangaScraper.BASE_URL + "manga/" + id
        }
    }

    fun fetchDetails(mangaId: String) {
        _isLoading.value = true
        _error.value = null
        
        val mangaUrl = decodeIdToUrl(mangaId)
        
        // Extract title from URL as fallback
        val parts = mangaUrl.trimEnd('/').split("/")
        val fallbackTitle = parts.lastOrNull()?.replace("-", " ")?.capitalize() ?: ""
        _mangaTitle.value = fallbackTitle
        
        MangaScraper.fetchMangaDetails(mangaUrl, object : MangaScraper.MangaDetailsCallback {
            override fun onSuccess(
                description: String?,
                status: String?,
                chapters: MutableList<com.fire.mangareader.domain.model.Chapter>?
            ) {
                _description.value = description ?: "لا يوجد وصف"
                _status.value = status ?: "غير معروف"
                _chapters.value = chapters ?: emptyList()
                _isLoading.value = false
            }

            override fun onError(errorMessage: String?) {
                _error.value = errorMessage ?: "حدث خطأ غير معروف"
                _isLoading.value = false
            }
        })
    }
}
