package com.fire.mangareader.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_lists")
data class CustomListEntity(
    @PrimaryKey
    val listId: String,
    val name: String,
    val description: String?,
    val thumbnail: String?
)
