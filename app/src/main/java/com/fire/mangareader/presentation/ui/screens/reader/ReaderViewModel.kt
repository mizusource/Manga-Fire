package com.fire.mangareader.presentation.ui.screens.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.network.ApiMangaScraper
import com.fire.mangareader.data.network.MangaScraper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReaderViewModel : ViewModel() {
    private val _pages = MutableStateFlow<List<String>>(emptyList())
    val pages: StateFlow<List<String>> = _pages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchPages(chapterId: String) {
        _isLoading.value = true
        _error.value = null
        
        // نفترض أن مسار القراءة يكون بهذا الشكل في موقع مانجا ليك
        // يتم استخدام API سريع للصور إذا توفر
        val chapterUrl = MangaScraper.BASE_URL + "manga/" + chapterId
        
        // نستخدم الطريقة المعتمدة في التطبيق لجلب الصور
        ApiMangaScraper.fetchChapterPagesFast(chapterUrl, object : MangaScraper.ChapterPagesCallback {
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
