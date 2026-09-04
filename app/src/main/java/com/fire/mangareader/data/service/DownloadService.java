package com.fire.mangareader.data.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.fire.mangareader.util.MangaDownloader;
import com.fire.mangareader.core.di.DownloadModule;
import com.fire.mangareader.data.download.DownloadManager;
import java.util.LinkedList;
import java.util.Queue;

public class DownloadService extends Service {
    private static final String CHANNEL_ID = "DownloadChannel";
    private NotificationManager notificationManager;
    private int notificationId = 1001;
    
    private static DownloadService instance;

    private static class DownloadTask {
        String mangaUrl;
        String chapterUrl;
        String chapterTitle;
        int startId;
        int notifId;
    }

    private Queue<DownloadTask> downloadQueue = new LinkedList<>();
    private boolean isDownloading = false;

    public static void startDownload(Context context, String mangaUrl, String chapterUrl, String chapterTitle) {
        MangaDownloader.isCancelled = false;
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

    public static void cancelAllDownloads(Context context) {
        if (instance != null) {
            instance.downloadQueue.clear();
            instance.isDownloading = false;
            instance.stopForeground(true);
            instance.stopSelf();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }
    }

    private PendingIntent getCancelIntent() {
        Intent cancelIntent = new Intent(this, NotificationReceiver.class);
        cancelIntent.setAction(NotificationReceiver.ACTION_CANCEL_DOWNLOAD);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getBroadcast(this, 0, cancelIntent, flags);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        String mangaUrl = intent.getStringExtra("mangaUrl");
        String chapterUrl = intent.getStringExtra("chapterUrl");
        String chapterTitle = intent.getStringExtra("chapterTitle");
        
        if (chapterUrl == null) return START_NOT_STICKY;

        DownloadTask task = new DownloadTask();
        task.mangaUrl = mangaUrl;
        task.chapterUrl = chapterUrl;
        task.chapterTitle = chapterTitle;
        task.startId = startId;
        task.notifId = notificationId++;
        
        downloadQueue.offer(task);
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("في الانتظار: " + chapterTitle)
                .setContentText("جاري الاستعداد...")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "إلغاء", getCancelIntent())
                .build();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(task.notifId, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(task.notifId, notification);
        }
        
        processNextTask();
        
        return START_NOT_STICKY;
    }

    private void processNextTask() {
        if (isDownloading) return;
        if (downloadQueue.isEmpty()) {
            stopForeground(true);
            stopSelf();
            return;
        }
        
        isDownloading = true;
        DownloadTask task = downloadQueue.poll();
        
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("جاري تحميل: " + task.chapterTitle)
                .setContentText("بدء التحميل...")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setProgress(100, 0, true)
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "إلغاء", getCancelIntent())
                .build();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(task.notifId, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
        } else {
            startForeground(task.notifId, notification);
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (MangaDownloader.isCancelled) {
                finishTaskAndProceed(task.startId);
                return;
            }
            
            MangaDownloader.downloadChapter(this, task.mangaUrl, task.chapterUrl, task.chapterTitle, new MangaDownloader.DownloadListener() {
                @Override
                public void onProgressUpdate(int current, int total) {
                    if (MangaDownloader.isCancelled) return;
                    int progress = (int) (((float) current / total) * 100);
                    DownloadModule.getInstance(DownloadService.this).provideDownloadManager().updateProgress(task.chapterUrl, progress);

                    Notification updated = new NotificationCompat.Builder(DownloadService.this, CHANNEL_ID)
                            .setContentTitle("جاري تحميل: " + task.chapterTitle)
                            .setContentText("صفحة " + current + " من " + total)
                            .setSmallIcon(android.R.drawable.stat_sys_download)
                            .setProgress(total, current, false)
                            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "إلغاء", getCancelIntent())
                            .build();
                    notificationManager.notify(task.notifId, updated);
                }

                @Override
                public void onSuccess() {
                    if (MangaDownloader.isCancelled) return;
                    DownloadModule.getInstance(DownloadService.this).provideDownloadManager().removeProgress(task.chapterUrl);

                    Notification success = new NotificationCompat.Builder(DownloadService.this, CHANNEL_ID)
                            .setContentTitle("اكتمل التحميل")
                            .setContentText(task.chapterTitle + " تم تحميله بنجاح")
                            .setSmallIcon(android.R.drawable.stat_sys_download_done)
                            .build();
                    notificationManager.notify(task.notifId, success);
                    finishTaskAndProceed(task.startId);
                }

                @Override
                public void onError(String errorMessage) {
                    if (MangaDownloader.isCancelled) return;
                    DownloadModule.getInstance(DownloadService.this).provideDownloadManager().removeProgress(task.chapterUrl);

                    Notification error = new NotificationCompat.Builder(DownloadService.this, CHANNEL_ID)
                            .setContentTitle("فشل التحميل")
                            .setContentText(task.chapterTitle + " - " + errorMessage)
                            .setSmallIcon(android.R.drawable.stat_notify_error)
                            .build();
                    notificationManager.notify(task.notifId, error);
                    finishTaskAndProceed(task.startId);
                }
            });
        }, 3000); 
    }
    
    private void finishTaskAndProceed(int startId) {
        isDownloading = false;
        if (downloadQueue.isEmpty()) {
            stopForeground(true);
            stopSelfResult(startId);
        } else {
            processNextTask();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
