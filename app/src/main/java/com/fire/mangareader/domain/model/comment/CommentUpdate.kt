package com.fire.mangareader.domain.model.comment

data class CommentUpdate(
    val commentId: String,
    val content: String,
    val isSpoiler: Boolean
)
