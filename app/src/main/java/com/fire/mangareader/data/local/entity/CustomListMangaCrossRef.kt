package com.fire.mangareader.data.local.entity

import androidx.room.Entity

@Entity(tableName = "custom_list_manga_cross_ref", primaryKeys = ["listId", "mangaUrl"])
data class CustomListMangaCrossRef(
    val listId: String,
    val mangaUrl: String,
    val addedAt: Long = System.currentTimeMillis()
)
