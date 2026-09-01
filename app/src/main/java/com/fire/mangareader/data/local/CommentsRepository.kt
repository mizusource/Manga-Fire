package com.fire.mangareader.data.local

import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CommentsRepository(private val commentDao: CommentDao) {

    fun getComments(mangaId: Int, sortOption: String): Flow<List<CommentEntity>> {
        return when (sortOption) {
            "newest" -> commentDao.getCommentsByNewest(mangaId)
            "oldest" -> commentDao.getCommentsByOldest(mangaId)
            "most_liked" -> commentDao.getCommentsByMostLiked(mangaId)
            else -> commentDao.getCommentsByNewest(mangaId)
        }
    }

    suspend fun addComment(mangaId: Int, content: String, isSpoiler: Boolean, userName: String = "You") {
        val newComment = CommentEntity(
            id = UUID.randomUUID().toString(),
            mangaId = mangaId,
            userId = "current_user_123",
            userName = userName,
            content = content,
            timestamp = System.currentTimeMillis(),
            isSpoiler = isSpoiler
        )
        commentDao.insertComment(newComment)
    }

    suspend fun toggleLike(comment: CommentEntity) {
        val newIsLiked = !comment.isLikedByUser
        val newLikes = if (newIsLiked) comment.likes + 1 else comment.likes - 1
        
        // If it was previously disliked, remove the dislike
        val newIsDisliked = if (newIsLiked) false else comment.isDislikedByUser
        val newDislikes = if (comment.isDislikedByUser && newIsLiked) comment.dislikes - 1 else comment.dislikes
        
        commentDao.updateReaction(comment.id, newLikes, newDislikes, newIsLiked, newIsDisliked)
    }

    suspend fun toggleDislike(comment: CommentEntity) {
        val newIsDisliked = !comment.isDislikedByUser
        val newDislikes = if (newIsDisliked) comment.dislikes + 1 else comment.dislikes - 1
        
        // If it was previously liked, remove the like
        val newIsLiked = if (newIsDisliked) false else comment.isLikedByUser
        val newLikes = if (comment.isLikedByUser && newIsDisliked) comment.likes - 1 else comment.likes
        
        commentDao.updateReaction(comment.id, newLikes, newDislikes, newIsLiked, newIsDisliked)
    }

    suspend fun deleteComment(commentId: String) {
        commentDao.deleteComment(commentId)
    }
}
