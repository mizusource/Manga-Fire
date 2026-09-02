package com.fire.mangareader.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class CommentsRepository(private val commentDao: CommentDao) {

    fun getComments(mangaUrl: String, sortOption: String): Flow<List<CommentEntity>> {
        return when (sortOption) {
            "newest" -> commentDao.getCommentsByNewest(mangaUrl)
            "oldest" -> commentDao.getCommentsByOldest(mangaUrl)
            "most_liked" -> commentDao.getCommentsByMostLiked(mangaUrl)
            else -> commentDao.getCommentsByNewest(mangaUrl)
        }
    }

    suspend fun addComment(mangaUrl: String, content: String, isSpoiler: Boolean, userName: String = "You") {
        val newComment = CommentEntity(
            id = UUID.randomUUID().toString(),
            mangaUrl = mangaUrl,
            userId = "current_user_123",
            userName = userName,
            content = content,
            timestamp = System.currentTimeMillis(),
            isSpoiler = isSpoiler
        )
        
        // Save locally
        commentDao.insertComment(newComment)

        // Send to Make.com Webhook
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://hook.eu1.make.com/ud10lj71nvofrtucj1jq5ls6te8jqtdc")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val jsonParam = JSONObject()
                jsonParam.put("commentId", newComment.id)
                jsonParam.put("mangaUrl", mangaUrl)
                jsonParam.put("userName", userName)
                jsonParam.put("content", content)
                jsonParam.put("isSpoiler", isSpoiler)
                jsonParam.put("timestamp", newComment.timestamp)

                val out = OutputStreamWriter(connection.outputStream)
                out.write(jsonParam.toString())
                out.flush()
                out.close()

                val responseCode = connection.responseCode
                println("Webhook Response Code: $responseCode")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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
