package com.fire.mangareader.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.fire.mangareader.utils.MangaDownloader;

public class DownloadService extends Service {
    private static final String CHANNEL_ID = "DownloadChannel";
    private NotificationManager notificationManager;
    private int notificationId = 1001;

    public static void startDownload(Context context, String mangaUrl, String chapterUrl, String chapterTitle) {
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra("mangaUrl", mangaUrl);
        intent.putExtra("chapterUrl", chapterUrl);
        intent.putExtra("chapterTitle", chapterTitle);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        String mangaUrl = intent.getStringExtra("mangaUrl");
        String chapterUrl = intent.getStringExtra("chapterUrl");
        String chapterTitle = intent.getStringExtra("chapterTitle");
        
        int currentId = notificationId++;

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("جاري تحميل: " + chapterTitle)
                .setContentText("بدء التحميل...")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setProgress(100, 0, true)
                .build();

        startForeground(currentId, notification);

        // Run download on main thread so WebView can be used inside MangaDownloader
        new Handler(Looper.getMainLooper()).post(() -> {
            MangaDownloader.downloadChapter(this, mangaUrl, chapterUrl, chapterTitle, new MangaDownloader.DownloadListener() {
                @Override
                public void onProgressUpdate(int current, int total) {
                    Notification updated = new NotificationCompat.Builder(DownloadService.this, CHANNEL_ID)
                            .setContentTitle("جاري تحميل: " + chapterTitle)
                            .setContentText("صفحة " + current + " من " + total)
                            .setSmallIcon(android.R.drawable.stat_sys_download)
                            .setProgress(total, current, false)
                            .build();
                    notificationManager.notify(currentId, updated);
                }

                @Override
                public void onSuccess() {
                    Notification success = new NotificationCompat.Builder(DownloadService.this, CHANNEL_ID)
                            .setContentTitle("اكتمل التحميل")
                            .setContentText(chapterTitle + " تم تحميله بنجاح")
                            .setSmallIcon(android.R.drawable.stat_sys_download_done)
                            .build();
                    notificationManager.notify(currentId, success);
                    stopSelfResult(startId);
                }

                @Override
                public void onError(String errorMessage) {
                    Notification error = new NotificationCompat.Builder(DownloadService.this, CHANNEL_ID)
                            .setContentTitle("فشل التحميل")
                            .setContentText(chapterTitle + " - " + errorMessage)
                            .setSmallIcon(android.R.drawable.stat_notify_error)
                            .build();
                    notificationManager.notify(currentId, error);
                    stopSelfResult(startId);
                }
            });
        });

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
