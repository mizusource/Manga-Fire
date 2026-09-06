import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

notif_logic = """
    private void checkNotificationStatus() {
        if (btnNotification == null) return;
        android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        isSubscribedToNotifications = prefs.getBoolean("notify_" + mangaUrl, false);
        btnNotification.setImageResource(isSubscribedToNotifications ? R.drawable.ic_notifications_active : R.drawable.ic_notifications_none);
        btnNotification.setColorFilter(isSubscribedToNotifications ? android.graphics.Color.parseColor("#39FF14") : android.graphics.Color.WHITE);
    }

    private void toggleNotificationSubscription() {
        if (btnNotification == null) return;
        isSubscribedToNotifications = !isSubscribedToNotifications;
        android.content.SharedPreferences prefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit().putBoolean("notify_" + mangaUrl, isSubscribedToNotifications).apply();
        
        btnNotification.setImageResource(isSubscribedToNotifications ? R.drawable.ic_notifications_active : R.drawable.ic_notifications_none);
        btnNotification.setColorFilter(isSubscribedToNotifications ? android.graphics.Color.parseColor("#39FF14") : android.graphics.Color.WHITE);
        
        String topic = "manga_" + mangaUrl.hashCode();
        if (isSubscribedToNotifications) {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) android.widget.Toast.makeText(this, "تم تفعيل إشعارات الفصول الجديدة", android.widget.Toast.LENGTH_SHORT).show();
                });
        } else {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) android.widget.Toast.makeText(this, "تم إلغاء إشعارات الفصول الجديدة", android.widget.Toast.LENGTH_SHORT).show();
                });
        }
    }
"""

if "toggleNotificationSubscription" not in content:
    content = content.replace("public class MangaDetailActivity extends AppCompatActivity {", "public class MangaDetailActivity extends AppCompatActivity {\n" + notif_logic)

content = content.replace("btnNotification = findViewById(R.id.btnNotification);\n        btnFavorite.setOnClickListener(v -> toggleFavorite());", "btnNotification = findViewById(R.id.btnNotification);\n        btnFavorite.setOnClickListener(v -> toggleFavorite());\n        btnNotification.setOnClickListener(v -> toggleNotificationSubscription());\n        checkNotificationStatus();")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

