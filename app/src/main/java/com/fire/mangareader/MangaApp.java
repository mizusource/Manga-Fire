package com.fire.mangareader;

import android.app.Application;
import com.fire.mangareader.util.AppAdminSettings;
import com.fire.mangareader.util.CacheManager;
import com.fire.mangareader.util.CrashHandler;
import com.google.firebase.messaging.FirebaseMessaging;
import android.util.Log;

public class MangaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        com.fire.mangareader.util.MangaOkHttp.init(this);
        // Initialize CrashHandler to prevent silent crashes and log them
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));

        // Initialize remote settings and listeners
        AppAdminSettings.initialize(this);

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

        // Clean old cache if exceeding 100MB
        new Thread(() -> CacheManager.autoTrimIfNeeded(this, 100)).start();
    }
}
