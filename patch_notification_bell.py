import re

xml_path = "app/src/main/res/layout/activity_manga_detail.xml"
with open(xml_path, "r") as f:
    xml_content = f.read()

bell_xml = """
                        <ImageView
                            android:id="@+id/btnNotification"
                            android:layout_width="44dp"
                            android:layout_height="44dp"
                            android:layout_marginStart="12dp"
                            android:padding="10dp"
                            android:src="@drawable/ic_notifications_none"
                            android:background="@drawable/bg_glass_icon"
                            app:tint="#FFFFFF" />
"""

xml_content = xml_content.replace('<ImageView\n                            android:id="@+id/btnFavorite"', bell_xml + '\n                        <ImageView\n                            android:id="@+id/btnFavorite"')

with open(xml_path, "w") as f:
    f.write(xml_content)

java_path = "app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java"
with open(java_path, "r") as f:
    java_content = f.read()

java_vars = """
    private ImageView btnNotification;
    private boolean isSubscribedToNotifications = false;
"""
java_content = java_content.replace("private ImageView btnFavorite;", "private ImageView btnFavorite;\n" + java_vars)

java_init = """
        btnFavorite = findViewById(R.id.btnFavorite);
        btnNotification = findViewById(R.id.btnNotification);
"""
java_content = java_content.replace("btnFavorite = findViewById(R.id.btnFavorite);", java_init)

java_logic = """
        if (btnNotification != null) {
            btnNotification.setOnClickListener(v -> toggleNotificationSubscription());
        }
"""
java_content = java_content.replace("btnFavorite.setOnClickListener(v -> toggleFavorite());", "btnFavorite.setOnClickListener(v -> toggleFavorite());\n        " + java_logic)

java_method = """
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
                    if (task.isSuccessful()) Toast.makeText(this, "تم تفعيل إشعارات الفصول الجديدة", Toast.LENGTH_SHORT).show();
                });
        } else {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) Toast.makeText(this, "تم إلغاء إشعارات الفصول الجديدة", Toast.LENGTH_SHORT).show();
                });
        }
    }
"""

java_content = java_content.replace("private void checkLibraryStatus() {", java_method + "\n    private void checkLibraryStatus() {")
java_content = java_content.replace("checkLibraryStatus();", "checkLibraryStatus();\n        checkNotificationStatus();")

with open(java_path, "w") as f:
    f.write(java_content)

