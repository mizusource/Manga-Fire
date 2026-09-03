package com.fire.mangareader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_history")
data class RecentManga(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String,
    val lastReadChapterId: String,
    val lastReadChapterName: String,
    val readAt: Long = System.currentTimeMillis()
)
