package com.fire.mangareader.utils;

import android.content.Context;
import android.util.Log;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.Locale;

public final class CacheManager {
    private static final String TAG = "CacheManager";

    public static long getCacheSizeBytes(Context context) {
        long size = 0;
        try {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null) size += getFolderSize(cacheDir);
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) size += getFolderSize(externalCacheDir);
            File photoCache = Glide.getPhotoCacheDir(context);
            if (photoCache != null) size += getFolderSize(photoCache);
        } catch (Exception e) {
            Log.e(TAG, "Error calculating cache size", e);
        }
        return size;
    }

    public static String getFormattedCacheSize(Context context) {
        long bytes = getCacheSizeBytes(context);
        if (bytes >= 1073741824) {
            return String.format(Locale.US, "%.2f غيغابايت", bytes / 1073741824.0);
        } else if (bytes >= 1048576) {
            return String.format(Locale.US, "%.1f ميجابايت", bytes / 1048576.0);
        } else if (bytes >= 1024) {
            return String.format(Locale.US, "%.0f كيلوبايت", bytes / 1024.0);
        }
        return bytes + " بايت";
    }

    public static boolean clearAppCache(Context context) {
        try {
            new Thread(() -> {
                try {
                    Glide.get(context).clearDiskCache();
                } catch (Exception ignored) {}
            }).start();
            Glide.get(context).clearMemory();

            File cacheDir = context.getCacheDir();
            if (cacheDir != null) deleteDirContents(cacheDir);
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) deleteDirContents(externalCacheDir);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error clearing cache", e);
            return false;
        }
    }

    public static void autoTrimIfNeeded(Context context, int maxMB) {
        long limitBytes = ((long) maxMB) * 1024L * 1024L;
        long currentBytes = getCacheSizeBytes(context);
        if (currentBytes > limitBytes) {
            Log.d(TAG, "Auto trimming cache from " + currentBytes + " bytes");
            clearAppCache(context);
        }
    }

    private static long getFolderSize(File file) {
        long size = 0;
        if (file == null || !file.exists()) return 0;
        if (file.isFile()) return file.length();
        File[] files = file.listFiles();
        if (files != null) {
            for (File f : files) {
                size += getFolderSize(f);
            }
        }
        return size;
    }

    private static boolean deleteDirContents(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) return true;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) deleteDirContents(f);
                f.delete();
            }
        }
        return true;
    }
}
