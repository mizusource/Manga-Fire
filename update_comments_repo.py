with open("app/src/main/java/com/fire/mangareader/data/local/CommentsRepository.kt", "r") as f:
    content = f.read()

import re

# We will modify the class constructor to accept Context
content = content.replace("class CommentsRepository(private val commentDao: CommentDao) {", "class CommentsRepository(private val commentDao: CommentDao, private val context: android.content.Context) {")

add_comment_old = """    suspend fun addComment(mangaUrl: String, content: String, isSpoiler: Boolean, userName: String = "You") {
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
    }"""

add_comment_new = """    suspend fun addComment(mangaUrl: String, content: String, isSpoiler: Boolean) {
        val supabaseManager = com.fire.mangareader.data.network.SupabaseManager.getInstance(context)
        val currentUserId = if (supabaseManager.isLoggedIn) supabaseManager.currentUserId else "guest_${UUID.randomUUID().toString().substring(0, 8)}"
        val userName = if (supabaseManager.isLoggedIn) "User" else "Guest"
        
        val newComment = CommentEntity(
            id = UUID.randomUUID().toString(),
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
        withContext(Dispatchers.IO) {
            try {
                val client = okhttp3.OkHttpClient()
                val jsonParam = JSONObject()
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
    }"""

content = content.replace(add_comment_old, add_comment_new)

with open("app/src/main/java/com/fire/mangareader/data/local/CommentsRepository.kt", "w") as f:
    f.write(content)
