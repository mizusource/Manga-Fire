package com.fire.mangareader.data.local.dao

import androidx.room.*
import com.fire.mangareader.data.local.entity.DownloadedChapter
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    @Query("SELECT * FROM downloaded_chapters ORDER BY timestamp DESC")
    fun getAllDownloads(): Flow<List<DownloadedChapter>>

    @Query("SELECT * FROM downloaded_chapters WHERE mangaId = :mangaId ORDER BY timestamp DESC")
    fun getDownloadsForManga(mangaId: String): Flow<List<DownloadedChapter>>

    @Query("SELECT * FROM downloaded_chapters WHERE chapterId = :chapterId")
    suspend fun getDownload(chapterId: String): DownloadedChapter?

    @Query("SELECT * FROM downloaded_chapters WHERE chapterId = :chapterId")
    fun getDownloadFlow(chapterId: String): Flow<DownloadedChapter?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadedChapter)

    @Update
    suspend fun updateDownload(download: DownloadedChapter)

    @Delete
    suspend fun deleteDownload(download: DownloadedChapter)
    
    @Query("DELETE FROM downloaded_chapters WHERE chapterId = :chapterId")
    suspend fun deleteDownloadById(chapterId: String)
}
