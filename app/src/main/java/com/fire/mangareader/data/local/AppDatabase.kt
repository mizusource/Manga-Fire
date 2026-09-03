package com.fire.mangareader.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.fire.mangareader.data.local.entity.FavoriteManga
import com.fire.mangareader.data.local.entity.RecentManga
import com.fire.mangareader.data.local.dao.FavoriteDao
import com.fire.mangareader.data.local.dao.RecentDao

@Database(entities = [CommentEntity::class, FavoriteManga::class, RecentManga::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun commentDao(): CommentDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun recentDao(): RecentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "manga_comments_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
