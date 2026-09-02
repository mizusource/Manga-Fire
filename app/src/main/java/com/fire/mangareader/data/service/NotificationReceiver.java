package com.fire.mangareader.data.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.fire.mangareader.util.MangaDownloader;

public class NotificationReceiver extends BroadcastReceiver {
    public static final String ACTION_CANCEL_DOWNLOAD = "com.fire.mangareader.ACTION_CANCEL_DOWNLOAD";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && ACTION_CANCEL_DOWNLOAD.equals(intent.getAction())) {
            // إيقاف التنزيل الحالي ومسح الطابور
            MangaDownloader.isCancelled = true;
            DownloadService.cancelAllDownloads(context);
            Toast.makeText(context, "تم إيقاف التنزيلات", Toast.LENGTH_SHORT).show();
        }
    }
}
