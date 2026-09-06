package com.fire.mangareader.data.local

import android.content.Context
import com.fire.mangareader.data.local.entity.FavoriteManga
import com.fire.mangareader.data.local.entity.RecentManga
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object DatabaseBridge {
    @JvmStatic
    fun toggleFavorite(context: Context, id: String, title: String, coverUrl: String, isFav: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(context).favoriteDao()
            if (isFav) {
                dao.insertFavorite(FavoriteManga(id, title, coverUrl, "", ""))
            } else {
                dao.deleteFavorite(id)
            }
        }
    }

    @JvmStatic
    fun addRecent(context: Context, id: String, title: String, coverUrl: String, chapterId: String, chapterTitle: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = AppDatabase.getDatabase(context).recentDao()
            dao.insertRecent(RecentManga(id, title, coverUrl, chapterId, chapterTitle, System.currentTimeMillis()))
        }
    }
}
