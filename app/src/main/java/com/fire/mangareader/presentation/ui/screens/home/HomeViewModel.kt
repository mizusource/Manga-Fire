package com.fire.mangareader.presentation.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.network.MangaScraper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _heroMangas = MutableStateFlow<List<UIManga>>(emptyList())
    val heroMangas: StateFlow<List<UIManga>> = _heroMangas

    private val _recentMangas = MutableStateFlow<List<UIManga>>(emptyList())
    val recentMangas: StateFlow<List<UIManga>> = _recentMangas

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchData()
    }

    fun fetchData() {
        _isLoading.value = true
        _error.value = null
        MangaScraper.fetchLatestFromAllSources(object : MangaScraper.ScrapingCallback {
            override fun onSuccess(mangas: MutableList<com.fire.mangareader.domain.model.Manga>?) {
                if (mangas == null || mangas.isEmpty()) {
                    _error.value = "لا توجد بيانات"
                    _isLoading.value = false
                    return
                }

                // تحويل لـ UIManga
                val uiMangas = mangas.map {
                    UIManga(
                        id = extractIdFromUrl(it.url),
                        title = it.title ?: "بدون عنوان",
                        coverUrl = it.coverUrl ?: "",
                        latestChapter = it.latestChapter ?: "",
                        rating = it.rating ?: ""
                    )
                }

                val heroList = if (uiMangas.size >= 5) uiMangas.take(5) else uiMangas
                val recentList = if (uiMangas.size > 5) uiMangas.drop(5) else uiMangas

                _heroMangas.value = heroList
                _recentMangas.value = recentList
                _isLoading.value = false
            }

            override fun onError(errorMessage: String?) {
                _error.value = errorMessage ?: "حدث خطأ غير معروف"
                _isLoading.value = false
            }
        })
    }

    private fun extractIdFromUrl(url: String?): String {
        if (url == null) return ""
        // عادة رابط المانجا بيكون mangalik.net/manga/manga-name
        val safeUrl = url.replace("https://", "").replace("http://", "")
        return android.util.Base64.encodeToString(url.toByteArray(), android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP or android.util.Base64.NO_PADDING)
    }
}
