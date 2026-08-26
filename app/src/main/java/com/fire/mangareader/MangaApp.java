package com.fire.mangareader;

import android.app.Application;
import com.fire.mangareader.utils.AppAdminSettings;
import com.fire.mangareader.utils.CacheManager;

public class MangaApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize remote settings and listeners
        AppAdminSettings.initialize(this);
        // Clean old cache if exceeding 100MB
        new Thread(() -> CacheManager.autoTrimIfNeeded(this, 100)).start();
    }
}
