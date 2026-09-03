with open("app/src/main/java/com/fire/mangareader/data/local/CommentsRepository.kt", "r") as f:
    content = f.read()

content = content.replace('val mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8")', '')
content = content.replace('val body = okhttp3.RequestBody.create(mediaType, jsonParam.toString())', 'val body = jsonParam.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())')

with open("app/src/main/java/com/fire/mangareader/data/local/CommentsRepository.kt", "w") as f:
    f.write(content)
