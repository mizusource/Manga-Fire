package com.fire.mangareader.presentation.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.network.MangaScraper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
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

    fun fetchDetails(mangaId: String) {
        _isLoading.value = true
        _error.value = null
        // نفترض أن مسار المانجا هو الرابط الأساسي + اسم المانجا (mangaId)
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
                _chapters.value = chapters?.reversed() ?: emptyList() // عكس الفصول لتظهر الأحدث أولاً
                _isLoading.value = false
            }

            override fun onError(errorMessage: String?) {
                _error.value = errorMessage ?: "حدث خطأ غير معروف"
                _isLoading.value = false
            }
        })
    }
}
