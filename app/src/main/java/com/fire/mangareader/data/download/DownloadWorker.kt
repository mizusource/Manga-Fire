package com.fire.mangareader.data.download

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import androidx.work.ForegroundInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fire.mangareader.data.local.AppDatabase
import com.fire.mangareader.data.local.entity.DownloadedChapter
import com.fire.mangareader.data.network.ApiMangaScraper
import com.fire.mangareader.data.network.MangaScraper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.resume

class DownloadWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val db = AppDatabase.getDatabase(context)
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val KEY_CHAPTER_ID = "chapter_id"
        const val KEY_MANGA_ID = "manga_id"
        const val KEY_MANGA_TITLE = "manga_title"
        const val KEY_CHAPTER_TITLE = "chapter_title"
        const val CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val chapterId = inputData.getString(KEY_CHAPTER_ID) ?: return@withContext Result.failure()
        val mangaId = inputData.getString(KEY_MANGA_ID) ?: return@withContext Result.failure()
        val mangaTitle = inputData.getString(KEY_MANGA_TITLE) ?: "Unknown"
        val chapterTitle = inputData.getString(KEY_CHAPTER_TITLE) ?: "Chapter"

        createNotificationChannel()

        val downloadDao = db.downloadDao()
        
        try {
            val existing = downloadDao.getDownload(chapterId)
            if (existing?.state == 2) {
                return@withContext Result.success()
            }

            var download = existing ?: DownloadedChapter(
                chapterId = chapterId,
                mangaId = mangaId,
                mangaTitle = mangaTitle,
                chapterTitle = chapterTitle,
                totalPages = 0,
                downloadedPages = 0,
                state = 1,
                downloadPath = ""
            )
            download = download.copy(state = 1)
            downloadDao.insertDownload(download)

            setForeground(createForegroundInfo(mangaTitle, chapterTitle, 0, 0))

            val pages = fetchPages(chapterId)
            if (pages.isEmpty()) {
                downloadDao.updateDownload(download.copy(state = 3))
                return@withContext Result.failure()
            }

            download = download.copy(totalPages = pages.size)
            downloadDao.updateDownload(download)

            val mangaDir = File(context.filesDir, "downloads/$mangaId")
            if (!mangaDir.exists()) mangaDir.mkdirs()
            val chapterDir = File(mangaDir, chapterId)
            if (!chapterDir.exists()) chapterDir.mkdirs()

            for ((index, url) in pages.withIndex()) {
                if (isStopped) {
                    downloadDao.updateDownload(download.copy(state = 0))
                    return@withContext Result.retry()
                }

                val file = File(chapterDir, "page_$index.jpg")
                if (!file.exists() || file.length() == 0L) {
                    val success = downloadImage(url, file)
                    if (!success) {
                        downloadDao.updateDownload(download.copy(state = 3))
                        return@withContext Result.failure()
                    }
                }

                download = download.copy(downloadedPages = index + 1)
                downloadDao.updateDownload(download)
                
                setForeground(createForegroundInfo(mangaTitle, chapterTitle, download.downloadedPages, download.totalPages))
            }

            downloadDao.updateDownload(download.copy(state = 2, downloadPath = chapterDir.absolutePath))
            return@withContext Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            val existing = downloadDao.getDownload(chapterId)
            if (existing != null) {
                downloadDao.updateDownload(existing.copy(state = 3))
            }
            return@withContext Result.failure()
        }
    }

    private suspend fun fetchPages(chapterId: String): List<String> = suspendCancellableCoroutine { cont ->
        val url = try {
            String(android.util.Base64.decode(chapterId, android.util.Base64.URL_SAFE))
        } catch (e: Exception) {
            MangaScraper.BASE_URL + "manga/" + chapterId
        }
        val chapterUrl = if (url.startsWith("http")) url else MangaScraper.BASE_URL + "manga/" + chapterId
        
        MangaScraper.fetchChapterPages(chapterUrl, object : MangaScraper.ChapterPagesCallback {
            override fun onSuccess(imageUrls: MutableList<String>?) {
                if (cont.isActive) cont.resume(imageUrls?.toList() ?: emptyList())
            }
            override fun onError(errorMessage: String?) {
                if (cont.isActive) cont.resume(emptyList())
            }
        })
    }

    private fun downloadImage(urlString: String, destination: File): Boolean {
        try {
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return false
            }

            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(destination)
            val buffer = ByteArray(4096)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.close()
            inputStream.close()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createForegroundInfo(mangaTitle: String, chapterTitle: String, progress: Int, max: Int): ForegroundInfo {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("جاري تحميل $mangaTitle")
            .setContentText(chapterTitle)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(max, progress, false)
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return ForegroundInfo(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            return ForegroundInfo(NOTIFICATION_ID, notification)
        }
    }
}
