package com.fire.mangareader.domain.model.comment

data class CommentRequest(
    val mangaId: Int,
    val content: String,
    val profile: String? = null,
    val userName: String? = null,
    val type: String,
    val commentId: String? = null,
    val isSpoiler: Boolean = false,
    val mentionedUserId: String? = null,
    val commentOwnerId: String? = null,
    val mangaTitle: String? = null
)
