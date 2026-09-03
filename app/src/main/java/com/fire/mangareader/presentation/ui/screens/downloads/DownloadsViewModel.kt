package com.fire.mangareader.presentation.ui.screens.downloads

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.download.DownloadManager
import com.fire.mangareader.data.local.entity.DownloadedChapter
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DownloadsViewModel(application: Application) : AndroidViewModel(application) {

    private val downloadManager = DownloadManager(application)

    val downloads: StateFlow<List<DownloadedChapter>> = downloadManager.getDownloadsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cancelDownload(chapterId: String) {
        viewModelScope.launch {
            downloadManager.cancelDownload(chapterId)
        }
    }

    fun deleteDownload(chapterId: String) {
        viewModelScope.launch {
            downloadManager.deleteDownload(chapterId)
        }
    }
}
