package com.fire.mangareader.domain.model.comment

data class CommentReport(
    val reportedUserId: String,
    val reason: String,
    val commentId: String
)
