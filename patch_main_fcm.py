with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

replacement = """
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Sync FCM token
        if (com.fire.mangareader.data.network.SupabaseManager.getInstance(this).isLoggedIn) {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    com.fire.mangareader.data.network.SupabaseManager.getInstance(this).updateFcmToken(token)
                }
            }
        }
"""

content = content.replace("    override fun onCreate(savedInstanceState: Bundle?) {\n        super.onCreate(savedInstanceState)", replacement)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
