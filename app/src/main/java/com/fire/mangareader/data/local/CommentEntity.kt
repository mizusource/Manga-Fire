package com.fire.mangareader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey val id: String,
    val mangaUrl: String,
    val userId: String,
    val userName: String,
    val userProfile: String? = null,
    val content: String,
    val timestamp: Long,
    val likes: Int = 0,
    val dislikes: Int = 0,
    val isSpoiler: Boolean = false,
    val isLikedByUser: Boolean = false,
    val isDislikedByUser: Boolean = false,
    val repliesCount: Int = 0,
    val type: String = "comment", // "comment" or "reply"
    val replyToId: String? = null
)
