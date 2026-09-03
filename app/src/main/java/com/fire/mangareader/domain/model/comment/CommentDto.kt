package com.fire.mangareader.domain.model.comment

data class CommentDto(
    val _id: String,
    val commentId: String?,
    val mangaId: Int,
    val userId: String,
    val userProfile: String?,
    val userName: String,
    val content: String,
    val type: String,
    val isSpoiler: Boolean = false,
    var likes: Int = 0,
    var dislikes: Int = 0,
    val replies: Int = 0,
    var isLikedByUser: Boolean? = null,
    var isDislikedByUser: Boolean? = null,
    val isCurrentUserComment: Boolean = false,
    val placeholder: String? = null,
    val role: Int? = null,
    val mentionedUserId: String? = null
)
