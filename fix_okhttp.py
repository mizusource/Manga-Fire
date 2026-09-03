import re

with open("app/src/main/java/com/fire/mangareader/data/local/CommentsRepository.kt", "r") as f:
    content = f.read()

content = content.replace("val body = okhttp3.RequestBody.create(okhttp3.MediaType.parse(\"application/json; charset=utf-8\"), jsonParam.toString())", 
"""val mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8")
                val body = okhttp3.RequestBody.create(mediaType, jsonParam.toString())""")

# In okhttp4, MediaType.parse is deprecated for Kotlin, use "application/json; charset=utf-8".toMediaTypeOrNull()
# And requestBody string extension is better
content = content.replace("""val body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonParam.toString())""",
"""val body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonParam.toString())""")

# Actually let's just do it cleanly
fix_media_type = """import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody"""

if "import okhttp3.MediaType.Companion.toMediaTypeOrNull" not in content:
    content = content.replace("import kotlinx.coroutines.withContext", "import kotlinx.coroutines.withContext\nimport okhttp3.MediaType.Companion.toMediaTypeOrNull\nimport okhttp3.RequestBody.Companion.toRequestBody")

content = content.replace('val body = okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonParam.toString())',
    'val body = jsonParam.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())')
    
content = content.replace('println("Webhook Response Code: ${response.code()}")',
    'println("Webhook Response Code: ${response.code}")')

with open("app/src/main/java/com/fire/mangareader/data/local/CommentsRepository.kt", "w") as f:
    f.write(content)
