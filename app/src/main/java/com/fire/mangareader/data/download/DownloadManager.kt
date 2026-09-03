package com.fire.mangareader.data.download

import android.content.Context
import androidx.work.*
import com.fire.mangareader.data.local.AppDatabase
import com.fire.mangareader.data.local.entity.DownloadedChapter
import kotlinx.coroutines.flow.Flow

class DownloadManager(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val downloadDao = db.downloadDao()
    private val workManager = WorkManager.getInstance(context)

    fun getDownloadsFlow(): Flow<List<DownloadedChapter>> {
        return downloadDao.getAllDownloads()
    }

    suspend fun enqueueDownload(
        chapterId: String,
        mangaId: String,
        mangaTitle: String,
        chapterTitle: String
    ) {
        val existing = downloadDao.getDownload(chapterId)
        if (existing?.state == 2) {
            // Already downloaded
            return
        }

        // Insert as QUEUED
        val download = DownloadedChapter(
            chapterId = chapterId,
            mangaId = mangaId,
            mangaTitle = mangaTitle,
            chapterTitle = chapterTitle,
            totalPages = 0,
            downloadedPages = 0,
            state = 0, // QUEUED
            downloadPath = ""
        )
        downloadDao.insertDownload(download)

        val data = workDataOf(
            DownloadWorker.KEY_CHAPTER_ID to chapterId,
            DownloadWorker.KEY_MANGA_ID to mangaId,
            DownloadWorker.KEY_MANGA_TITLE to mangaTitle,
            DownloadWorker.KEY_CHAPTER_TITLE to chapterTitle
        )

        val request = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(data)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        workManager.enqueueUniqueWork(
            "download_$chapterId",
            ExistingWorkPolicy.KEEP,
            request
        )
    }

    suspend fun cancelDownload(chapterId: String) {
        workManager.cancelUniqueWork("download_$chapterId")
        val existing = downloadDao.getDownload(chapterId)
        if (existing != null) {
            downloadDao.updateDownload(existing.copy(state = 3)) // Error/Canceled
        }
    }

    suspend fun deleteDownload(chapterId: String) {
        cancelDownload(chapterId)
        val existing = downloadDao.getDownload(chapterId)
        if (existing != null) {
            val file = java.io.File(existing.downloadPath)
            if (file.exists()) {
                file.deleteRecursively()
            }
            downloadDao.deleteDownloadById(chapterId)
        }
    }
}
