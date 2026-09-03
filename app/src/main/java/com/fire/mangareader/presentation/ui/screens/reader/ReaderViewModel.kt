package com.fire.mangareader.presentation.ui.screens.reader

import androidx.lifecycle.ViewModel
import com.fire.mangareader.data.network.ApiMangaScraper
import com.fire.mangareader.data.network.MangaScraper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReaderViewModel : ViewModel() {

    private val _pages = MutableStateFlow<List<String>>(emptyList())
    val pages: StateFlow<List<String>> = _pages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private fun decodeIdToUrl(id: String): String {
        return try {
            val decoded = String(android.util.Base64.decode(id, android.util.Base64.URL_SAFE))
            if (decoded.startsWith("http")) decoded else MangaScraper.BASE_URL + "manga/" + id
        } catch (e: Exception) {
            MangaScraper.BASE_URL + "manga/" + id
        }
    }

    fun fetchPages(chapterId: String) {
        _isLoading.value = true
        _error.value = null
        
        val chapterUrl = decodeIdToUrl(chapterId)
        
        MangaScraper.fetchChapterPages(chapterUrl, object : MangaScraper.ChapterPagesCallback {
            override fun onSuccess(imageUrls: MutableList<String>?) {
                _pages.value = imageUrls ?: emptyList()
                _isLoading.value = false
            }

            override fun onError(errorMessage: String?) {
                _error.value = errorMessage ?: "حدث خطأ غير معروف"
                _isLoading.value = false
            }
        })
    }
}
