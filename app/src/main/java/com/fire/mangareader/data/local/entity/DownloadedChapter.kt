package com.fire.mangareader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_chapters")
data class DownloadedChapter(
    @PrimaryKey val chapterId: String,
    val mangaId: String,
    val mangaTitle: String,
    val chapterTitle: String,
    val totalPages: Int,
    val downloadedPages: Int,
    val state: Int, // 0: QUEUED, 1: DOWNLOADING, 2: DOWNLOADED, 3: ERROR
    val downloadPath: String,
    val timestamp: Long = System.currentTimeMillis()
)
