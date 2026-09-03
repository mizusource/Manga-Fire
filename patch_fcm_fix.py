with open("app/src/main/java/com/fire/mangareader/data/service/MyFirebaseMessagingService.kt", "r") as f:
    content = f.read()

content = content.replace("    override fun onNewToken(token: String) {\n    override fun onNewToken(token: String) {", "    override fun onNewToken(token: String) {")

with open("app/src/main/java/com/fire/mangareader/data/service/MyFirebaseMessagingService.kt", "w") as f:
    f.write(content)
