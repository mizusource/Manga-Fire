package com.fire.mangareader.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class DownloadNotificationHelper {
    public static final String CHANNEL_ID = "manga_downloads_channel";
    public static final String CHANNEL_NAME = "تنزيل الفصول";
    private final Context context;
    private final NotificationManager notificationManager;

    public DownloadNotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("إشعارات تقدم تنزيل فصول المانجا في الخلفية");
            channel.setSound(null, null);
            channel.enableVibration(false);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void showDownloadProgress(int notificationId, String mangaTitle, String chapterTitle, int progressPercent, String qualityBadge) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle("جاري تنزيل " + mangaTitle)
                    .setContentText(chapterTitle + " - " + progressPercent + "% (" + qualityBadge + ")")
                    .setProgress(100, progressPercent, false)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setPriority(NotificationCompat.PRIORITY_LOW);

            if (notificationManager != null) {
                notificationManager.notify(notificationId, builder.build());
            }
        } catch (Exception ignored) {}
    }

    public void showDownloadComplete(int notificationId, String mangaTitle, String chapterTitle, String qualityBadge) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setContentTitle("تم تنزيل الفصل بنجاح 🎉")
                    .setContentText(mangaTitle + " - " + chapterTitle + " (" + qualityBadge + ")")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            if (notificationManager != null) {
                notificationManager.notify(notificationId, builder.build());
            }
        } catch (Exception ignored) {}
    }

    public void showDownloadFailed(int notificationId, String mangaTitle, String chapterTitle) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_notify_error)
                    .setContentTitle("فشل تنزيل " + chapterTitle)
                    .setContentText("حدث خطأ أثناء تحميل صور الفصل (" + mangaTitle + ").")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true);

            if (notificationManager != null) {
                notificationManager.notify(notificationId, builder.build());
            }
        } catch (Exception ignored) {}
    }

    public void cancelNotification(int notificationId) {
        try {
            if (notificationManager != null) {
                notificationManager.cancel(notificationId);
            }
        } catch (Exception ignored) {}
    }
}
