package com.fire.mangareader.utils

import android.content.Context
import java.io.File

object DownloadChecker {
    
    @JvmStatic
    fun getMangaDir(context: Context, mangaUrl: String): File {
        return File(context.filesDir, mangaUrl.hashCode().toString())
    }

    @JvmStatic
    fun getChapterDir(context: Context, mangaUrl: String, chapterUrl: String): File {
        val mangaFolder = getMangaDir(context, mangaUrl)
        return File(mangaFolder, chapterUrl.hashCode().toString())
    }

    @JvmStatic
    fun isChapterDownloaded(context: Context, mangaUrl: String, chapterUrl: String): Boolean {
        val chapterFolder = getChapterDir(context, mangaUrl, chapterUrl)
        val files = chapterFolder.listFiles()
        return chapterFolder.exists() && chapterFolder.isDirectory && files != null && files.isNotEmpty()
    }

    @JvmStatic
    fun getDownloadedImages(context: Context, mangaUrl: String, chapterUrl: String): List<File> {
        val chapterFolder = getChapterDir(context, mangaUrl, chapterUrl)
        val files = chapterFolder.listFiles() ?: return emptyList()
        
        return files.filter { it.isFile && (it.extension == "jpg" || it.extension == "png" || it.extension == "webp" || it.extension == "jpeg") }
            .sortedBy { 
                it.nameWithoutExtension.toIntOrNull() ?: Int.MAX_VALUE 
            }
    }
}
