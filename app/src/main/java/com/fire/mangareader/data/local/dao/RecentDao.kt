package com.fire.mangareader.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.fire.mangareader.data.local.entity.RecentManga
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDao {
    @Query("SELECT * FROM recent_history ORDER BY readAt DESC")
    fun getRecentHistory(): Flow<List<RecentManga>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecent(manga: RecentManga)

    @Query("DELETE FROM recent_history")
    suspend fun clearHistory()

    @Query("DELETE FROM recent_history WHERE id = :id")
    suspend fun deleteRecent(id: String)
}
