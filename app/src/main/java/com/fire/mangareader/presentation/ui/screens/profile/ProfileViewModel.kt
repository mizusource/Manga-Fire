package com.fire.mangareader.presentation.ui.screens.profile

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fire.mangareader.data.local.AppDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val favoriteDao = db.favoriteDao()
    private val recentDao = db.recentDao()
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    val favoritesCount = favoriteDao.getAllFavorites().map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val historyCount = recentDao.getRecentHistory().map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _userName = MutableStateFlow(prefs.getString("user_name", "أوتاكو") ?: "أوتاكو")
    val userName: StateFlow<String> = _userName

    private val _profileImageUri = MutableStateFlow(prefs.getString("profile_image", "") ?: "")
    val profileImageUri: StateFlow<String> = _profileImageUri

    fun updateUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
        _userName.value = name
    }

    fun updateProfileImage(uri: String) {
        prefs.edit().putString("profile_image", uri).apply()
        _profileImageUri.value = uri
    }
}
