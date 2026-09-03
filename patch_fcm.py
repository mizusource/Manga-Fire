with open("app/src/main/java/com/fire/mangareader/data/service/MyFirebaseMessagingService.kt", "r") as f:
    content = f.read()

replacement = """
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.fire.mangareader.data.local.AppDatabase
import com.fire.mangareader.data.local.entity.NotificationEntity

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
"""

content = content.replace("class MyFirebaseMessagingService : FirebaseMessagingService() {", replacement.strip())

replacement2 = """
        showNotification(title, message)

        // Save to DB
        val db = AppDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            db.notificationDao().insertNotification(
                NotificationEntity(
                    title = title,
                    body = message
                )
            )
        }
    }
"""

content = content.replace("        showNotification(title, message)\n    }", replacement2)

with open("app/src/main/java/com/fire/mangareader/data/service/MyFirebaseMessagingService.kt", "w") as f:
    f.write(content)
