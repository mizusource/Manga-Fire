package com.fire.mangareader.core.di;

import android.content.Context;
import com.fire.mangareader.data.download.DownloadManager;
import com.fire.mangareader.data.preference.PreferencesHelper;
import com.google.gson.Gson;

/**
 * Manages Dependency Injection for Download related classes.
 * Similar to MangaSlayer's DownloadModule.
 */
public class DownloadModule {
    
    private static DownloadModule instance;
    private final Context applicationContext;
    
    private PreferencesHelper preferencesHelper;
    private DownloadManager downloadManager;
    private Gson gson;

    private DownloadModule(Context context) {
        this.applicationContext = context.getApplicationContext();
    }

    public static synchronized DownloadModule getInstance(Context context) {
        if (instance == null) {
            instance = new DownloadModule(context);
        }
        return instance;
    }

    public synchronized PreferencesHelper providePreferencesHelper() {
        if (preferencesHelper == null) {
            preferencesHelper = new PreferencesHelper(applicationContext);
        }
        return preferencesHelper;
    }

    public synchronized DownloadManager provideDownloadManager() {
        if (downloadManager == null) {
            downloadManager = new DownloadManager(applicationContext);
        }
        return downloadManager;
    }

    public synchronized Gson provideGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
