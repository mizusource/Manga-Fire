package com.fire.mangareader.ui.comments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.local.AppDatabase
import com.fire.mangareader.data.local.CommentEntity
import com.fire.mangareader.data.local.CommentsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommentsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CommentsRepository

    val currentMangaId = MutableStateFlow(1) // Default manga ID
    val sortOption = MutableStateFlow("newest") // newest, oldest, most_liked

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CommentsRepository(database.commentDao())
        
        // Seed some data for demo if needed, but we'll let the user add them
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val comments: StateFlow<List<CommentEntity>> = sortOption.flatMapLatest { sort ->
        repository.getComments(currentMangaId.value, sort)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun addComment(content: String, isSpoiler: Boolean) {
        viewModelScope.launch {
            repository.addComment(currentMangaId.value, content, isSpoiler)
        }
    }

    fun toggleLike(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleLike(comment)
        }
    }

    fun toggleDislike(comment: CommentEntity) {
        viewModelScope.launch {
            repository.toggleDislike(comment)
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            repository.deleteComment(commentId)
        }
    }

    fun changeSortOption(option: String) {
        sortOption.value = option
    }
}
