import re

with open("app/src/main/java/com/fire/mangareader/data/local/CommentsRepository.kt", "r") as f:
    content = f.read()

# Replace addComment
start_idx = content.find("suspend fun addComment")
end_idx = content.find("suspend fun toggleLike", start_idx)

new_add_comment = """suspend fun addComment(mangaUrl: String, content: String, isSpoiler: Boolean) {
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
                
                val body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonParam.toString())
                val request = okhttp3.Request.Builder()
                    .url("https://hook.eu1.make.com/ud10lj71nvofrtucj1jq5ls6te8jqtdc")
                    .post(body)
                    .build()
                
                client.newCall(request).execute().use { response ->
                    println("Webhook Response Code: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    """

if start_idx != -1 and end_idx != -1:
    content = content[:start_idx] + new_add_comment + content[end_idx:]

with open("app/src/main/java/com/fire/mangareader/data/local/CommentsRepository.kt", "w") as f:
    f.write(content)
