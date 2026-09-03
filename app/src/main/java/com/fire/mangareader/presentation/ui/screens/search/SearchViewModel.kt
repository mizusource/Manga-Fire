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

class SearchViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val searchResults: Flow<PagingData<UIManga>> = _searchQuery
        .debounce(500)
        .filter { it.length >= 3 || it.isEmpty() }
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(PagingData.empty())
            } else {
                Pager(
                    config = PagingConfig(pageSize = 20, enablePlaceholders = false),
                    pagingSourceFactory = { MangaPagingSource(query) }
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

    fun onQueryChange(query: String) {
        _searchQuery.value = query
    }

    private fun extractIdFromUrl(url: String?): String {
        if (url == null) return ""
        val parts = url.trimEnd('/').split("/")
        return parts.lastOrNull() ?: ""
    }
}
