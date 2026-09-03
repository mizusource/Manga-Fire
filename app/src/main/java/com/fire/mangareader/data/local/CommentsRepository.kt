package com.fire.mangareader.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class CommentsRepository(private val commentDao: CommentDao, private val context: android.content.Context) {

    fun getComments(mangaUrl: String, sortOption: String): Flow<List<CommentEntity>> {
        return when (sortOption) {
            "newest" -> commentDao.getCommentsByNewest(mangaUrl)
            "oldest" -> commentDao.getCommentsByOldest(mangaUrl)
            "most_liked" -> commentDao.getCommentsByMostLiked(mangaUrl)
            else -> commentDao.getCommentsByNewest(mangaUrl)
        }
    }

    suspend fun addComment(mangaUrl: String, content: String, isSpoiler: Boolean) {
        val supabaseManager = com.fire.mangareader.data.network.SupabaseManager.getInstance(context)
        val currentUserId = if (supabaseManager.isLoggedIn) supabaseManager.currentUserId else "guest_${java.util.UUID.randomUUID().toString().substring(0, 8)}"
        val userName = if (supabaseManager.isLoggedIn) "User" else "Guest"
        
        val newComment = CommentEntity(
            id = java.util.UUID.randomUUID().toString(),
            mangaUrl = mangaUrl,
            userId = currentUserId,
            userName = userName,
            content = content,
            timestamp = System.currentTimeMillis(),
            isSpoiler = isSpoiler
        )
        
        // Save locally
        commentDao.insertComment(newComment)

        // Send to Make.com Webhook
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                val jsonParam = org.json.JSONObject()
                jsonParam.put("commentId", newComment.id)
                jsonParam.put("mangaUrl", mangaUrl)
                jsonParam.put("userId", currentUserId)
                jsonParam.put("userName", userName)
                jsonParam.put("content", content)
                jsonParam.put("isSpoiler", isSpoiler)
                jsonParam.put("timestamp", newComment.timestamp)
                
                
                val body = jsonParam.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
                val request = okhttp3.Request.Builder()
                    .url("https://hook.eu1.make.com/ud10lj71nvofrtucj1jq5ls6te8jqtdc")
                    .post(body)
                    .build()
                
                client.newCall(request).execute().use { response ->
                    println("Webhook Response Code: ${response.code}")
                }
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
