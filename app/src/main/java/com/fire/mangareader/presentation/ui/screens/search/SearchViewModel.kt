package com.fire.mangareader.presentation.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.fire.mangareader.data.network.MangaPagingSource
import com.fire.mangareader.presentation.ui.screens.home.UIManga
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

data class SearchFilter(
    val query: String = "",
    val status: String = "",
    val type: String = "",
    val genres: List<String> = emptyList()
)

class SearchViewModel : ViewModel() {
    private val _filter = MutableStateFlow(SearchFilter())
    val filter: StateFlow<SearchFilter> = _filter

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<UIManga>> = _filter
        .debounce(500)
        .flatMapLatest { currentFilter ->
            if (currentFilter.query.isEmpty() && currentFilter.status.isEmpty() && currentFilter.type.isEmpty() && currentFilter.genres.isEmpty()) {
                flowOf(PagingData.empty())
            } else {
                Pager(
                    config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                    pagingSourceFactory = { MangaPagingSource(currentFilter.query, currentFilter.genres, currentFilter.status, currentFilter.type) }
                ).flow.map { pagingData ->
                    pagingData.map { manga ->
                        UIManga(
                            id = extractIdFromUrl(manga.url),
                            title = manga.title ?: "بدون عنوان",
                            coverUrl = manga.coverUrl ?: "",
                            latestChapter = manga.latestChapter ?: "",
                            rating = manga.rating ?: ""
                        )
                    }
                }
            }
        }
        .cachedIn(viewModelScope)

    fun onQueryChange(query: String) { _filter.update { it.copy(query = query) } }
    
    fun onStatusChange(status: String) { 
        _filter.update { it.copy(status = if (it.status == status) "" else status) } 
    }
    
    fun onTypeChange(type: String) { 
        _filter.update { it.copy(type = if (it.type == type) "" else type) } 
    }
    
    fun toggleGenre(genre: String) {
        _filter.update {
            val newGenres = if (it.genres.contains(genre)) {
                it.genres - genre
            } else {
                it.genres + genre
            }
            it.copy(genres = newGenres)
        }
    }
    
    fun clearFilters() {
        _filter.update { it.copy(status = "", type = "", genres = emptyList()) }
    }

    private fun extractIdFromUrl(url: String?): String {
        if (url == null) return ""
        val safeUrl = url
        return android.util.Base64.encodeToString(url.toByteArray(), android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP or android.util.Base64.NO_PADDING)
    }
}
