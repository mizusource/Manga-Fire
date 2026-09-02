package com.fire.mangareader;

import android.app.Application;
import com.fire.mangareader.util.AppAdminSettings;
import com.fire.mangareader.util.CacheManager;
import com.fire.mangareader.util.CrashHandler;

public class MangaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize CrashHandler to prevent silent crashes and log them
        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler(this));

        // Initialize remote settings and listeners
        AppAdminSettings.initialize(this);
        // Clean old cache if exceeding 100MB
        new Thread(() -> CacheManager.autoTrimIfNeeded(this, 100)).start();
    }
}
