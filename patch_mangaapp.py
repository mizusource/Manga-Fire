import re

filepath = 'app/src/main/java/com/fire/mangareader/MangaApp.java'
with open(filepath, 'r') as f:
    content = f.read()

import_statement = "import com.google.firebase.messaging.FirebaseMessaging;\nimport android.util.Log;"

if 'FirebaseMessaging' not in content:
    content = content.replace('import com.fire.mangareader.util.CrashHandler;', 'import com.fire.mangareader.util.CrashHandler;\n' + import_statement)

init_statement = """
        // Subscribe to Firebase Cloud Messaging topic
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("manga_updates")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("MangaApp", "FCM Topic Subscription Failed");
                    } else {
                        Log.d("MangaApp", "Subscribed to manga_updates topic successfully");
                    }
                });
        } catch (Exception e) {
            Log.e("MangaApp", "Firebase not initialized properly", e);
        }
"""

if 'subscribeToTopic' not in content:
    content = content.replace('AppAdminSettings.initialize(this);', 'AppAdminSettings.initialize(this);\n' + init_statement)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MangaApp.java")
