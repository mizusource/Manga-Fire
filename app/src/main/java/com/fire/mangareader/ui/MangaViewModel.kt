package com.fire.mangareader.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.MangaRepository
import com.fire.mangareader.model.Chapter
import com.fire.mangareader.model.MangaDetails
import com.fire.mangareader.model.MangaItem
import com.fire.mangareader.model.MangaSourceId
import com.fire.mangareader.model.ReaderPage
import com.fire.mangareader.model.ReadingProgress
import com.fire.mangareader.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String, val isCloudflare: Boolean = false, val blockedUrl: String? = null) : UiState<Nothing>
}

class MangaViewModel(application: Application) : AndroidViewModel(application) {

    val repository = MangaRepository(application)

    // Current source
    private val _selectedSource = MutableStateFlow(MangaSourceId.MANGA_STARZ)
    val selectedSource: StateFlow<MangaSourceId> = _selectedSource.asStateFlow()

    // Explore list
    private val _mangaListState = MutableStateFlow<UiState<List<MangaItem>>>(UiState.Loading)
    val mangaListState: StateFlow<UiState<List<MangaItem>>> = _mangaListState.asStateFlow()

    // Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchState = MutableStateFlow<UiState<List<MangaItem>>>(UiState.Idle)
    val searchState: StateFlow<UiState<List<MangaItem>>> = _searchState.asStateFlow()

    // Details
    private val _detailsState = MutableStateFlow<UiState<MangaDetails>>(UiState.Idle)
    val detailsState: StateFlow<UiState<MangaDetails>> = _detailsState.asStateFlow()

    // Reader
    private val _readerState = MutableStateFlow<UiState<List<ReaderPage>>>(UiState.Idle)
    val readerState: StateFlow<UiState<List<ReaderPage>>> = _readerState.asStateFlow()

    private val _currentChapter = MutableStateFlow<Chapter?>(null)
    val currentChapter: StateFlow<Chapter?> = _currentChapter.asStateFlow()

    // Cloudflare resolver trigger
    private val _cloudflareTargetUrl = MutableStateFlow<String?>(null)
    val cloudflareTargetUrl: StateFlow<String?> = _cloudflareTargetUrl.asStateFlow()

    init {
        loadMangaList()
    }

    fun setSource(source: MangaSourceId) {
        if (_selectedSource.value != source) {
            _selectedSource.value = source
            loadMangaList()
            if (_searchQuery.value.isNotBlank()) {
                performSearch(_searchQuery.value)
            }
        }
    }

    fun loadMangaList(order: String = "latest") {
        viewModelScope.launch {
            _mangaListState.value = UiState.Loading
            try {
                val items = repository.getMangaList(_selectedSource.value, 1, order)
                if (items.isEmpty()) {
                    _mangaListState.value = UiState.Error("لم يتم العثور على أعمال في هذا المصدر.")
                } else {
                    _mangaListState.value = UiState.Success(items)
                }
            } catch (e: NetworkClient.CloudflareBlockedException) {
                _mangaListState.value = UiState.Error("حماية Cloudflare مفعلة", isCloudflare = true, blockedUrl = e.url)
            } catch (e: Exception) {
                _mangaListState.value = UiState.Error(e.localizedMessage ?: "حدث خطأ أثناء تحميل القائمة.")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchState.value = UiState.Idle
        }
    }

    fun performSearch(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _searchState.value = UiState.Loading
            try {
                val items = repository.searchManga(_selectedSource.value, query)
                if (items.isEmpty()) {
                    _searchState.value = UiState.Error("لا توجد نتائج بحث مطابقة.")
                } else {
                    _searchState.value = UiState.Success(items)
                }
            } catch (e: NetworkClient.CloudflareBlockedException) {
                _searchState.value = UiState.Error("حماية Cloudflare مفعلة", isCloudflare = true, blockedUrl = e.url)
            } catch (e: Exception) {
                _searchState.value = UiState.Error(e.localizedMessage ?: "فشل في عملية البحث.")
            }
        }
    }

    fun loadMangaDetails(manga: MangaItem) {
        viewModelScope.launch {
            _detailsState.value = UiState.Loading
            try {
                val details = repository.getMangaDetails(manga.sourceId, manga.url)
                _detailsState.value = UiState.Success(details)
            } catch (e: NetworkClient.CloudflareBlockedException) {
                _detailsState.value = UiState.Error("حماية Cloudflare مفعلة", isCloudflare = true, blockedUrl = e.url)
            } catch (e: Exception) {
                _detailsState.value = UiState.Error(e.localizedMessage ?: "فشل في تحميل تفاصيل المانجا.")
            }
        }
    }

    fun loadChapterPages(chapter: Chapter, manga: MangaDetails) {
        _currentChapter.value = chapter
        viewModelScope.launch {
            _readerState.value = UiState.Loading
            try {
                val pages = repository.getChapterPages(manga.sourceId, chapter.url)
                if (pages.isEmpty()) {
                    _readerState.value = UiState.Error("لم يتم العثور على صفحات في هذا الفصل.")
                } else {
                    _readerState.value = UiState.Success(pages)
                    // Save reading progress
                    repository.saveProgress(
                        ReadingProgress(
                            mangaId = manga.id,
                            chapterId = chapter.id,
                            chapterName = chapter.name,
                            pageIndex = 0
                        )
                    )
                }
            } catch (e: NetworkClient.CloudflareBlockedException) {
                _readerState.value = UiState.Error("حماية Cloudflare مفعلة", isCloudflare = true, blockedUrl = e.url)
            } catch (e: Exception) {
                _readerState.value = UiState.Error(e.localizedMessage ?: "فشل في تحميل صور الفصل.")
            }
        }
    }

    fun triggerCloudflareBypass(url: String) {
        _cloudflareTargetUrl.value = url
    }

    fun onCloudflareResolved() {
        val url = _cloudflareTargetUrl.value
        _cloudflareTargetUrl.value = null
        // Retry current operation
        if (_detailsState.value is UiState.Error) {
            val cur = (_detailsState.value as UiState.Error).blockedUrl
            if (cur != null) {
                // Retry details
                viewModelScope.launch {
                    try {
                        val details = repository.getMangaDetails(_selectedSource.value, cur)
                        _detailsState.value = UiState.Success(details)
                    } catch (e: Exception) {
                        _detailsState.value = UiState.Error(e.localizedMessage ?: "حدث خطأ أثناء المحاولة.")
                    }
                }
            }
        } else if (_readerState.value is UiState.Error) {
            val ch = _currentChapter.value
            val det = (_detailsState.value as? UiState.Success)?.data
            if (ch != null && det != null) {
                loadChapterPages(ch, det)
            }
        } else {
            loadMangaList()
        }
    }

    fun dismissCloudflare() {
        _cloudflareTargetUrl.value = null
    }

    fun toggleFavorite(manga: MangaItem) {
        repository.toggleFavorite(manga)
    }

    fun isFavorite(mangaId: String): Boolean {
        return repository.isFavorite(mangaId)
    }
}
