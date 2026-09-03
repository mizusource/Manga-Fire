with open("app/src/main/java/com/fire/mangareader/data/service/MyFirebaseMessagingService.kt", "r") as f:
    content = f.read()

replacement = """
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Here you would send the token to your backend (e.g. Supabase)
        val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
        
        com.fire.mangareader.data.network.SupabaseManager.getInstance(this).updateFcmToken(token)
    }
"""

import re
content = re.sub(r'    override fun onNewToken\(token: String\) \{.*?prefs\.edit\(\)\.putString\("fcm_token", token\)\.apply\(\)\n    \}', replacement.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/data/service/MyFirebaseMessagingService.kt", "w") as f:
    f.write(content)
