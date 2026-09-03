package com.fire.mangareader.presentation.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.network.MangaScraper
import com.fire.mangareader.presentation.ui.screens.home.UIManga
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<UIManga>>(emptyList())
    val searchResults: StateFlow<List<UIManga>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.length < 3) {
            _searchResults.value = emptyList()
            _error.value = null
            _isLoading.value = false
            return
        }

        searchJob = viewModelScope.launch {
            delay(500) // Debounce for 500ms
            performSearch(query)
        }
    }

    private fun performSearch(query: String) {
        _isLoading.value = true
        _error.value = null

        MangaScraper.searchManga(query, object : MangaScraper.ScrapingCallback {
            override fun onSuccess(mangas: MutableList<com.fire.mangareader.domain.model.Manga>?) {
                if (mangas == null || mangas.isEmpty()) {
                    _error.value = "لا توجد نتائج للبحث"
                    _searchResults.value = emptyList()
                } else {
                    _searchResults.value = mangas.map {
                        UIManga(
                            id = extractIdFromUrl(it.url),
                            title = it.title ?: "بدون عنوان",
                            coverUrl = it.coverUrl ?: "",
                            latestChapter = it.latestChapter ?: "",
                            rating = it.rating ?: ""
                        )
                    }
                    _error.value = null
                }
                _isLoading.value = false
            }

            override fun onError(errorMessage: String?) {
                _error.value = errorMessage ?: "حدث خطأ أثناء البحث"
                _isLoading.value = false
            }
        })
    }

    private fun extractIdFromUrl(url: String?): String {
        if (url == null) return ""
        val parts = url.trimEnd('/').split("/")
        return parts.lastOrNull() ?: ""
    }
}
