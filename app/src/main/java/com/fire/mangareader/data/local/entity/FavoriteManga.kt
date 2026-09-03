package com.fire.mangareader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteManga(
    @PrimaryKey val id: String,
    val title: String,
    val coverUrl: String,
    val latestChapter: String,
    val rating: String,
    val addedAt: Long = System.currentTimeMillis()
)
