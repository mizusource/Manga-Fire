package com.fire.mangareader.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.fire.mangareader.R;
import com.fire.mangareader.activity.MainActivity;
import com.fire.mangareader.database.AppDatabase;
import com.fire.mangareader.database.LibraryItem;

import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateCheckWorker extends Worker {

    public UpdateCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            List<LibraryItem> favorites = db.mangaDao().getAllFavorites();
            if (favorites == null || favorites.isEmpty()) return Result.success();

            OkHttpClient client = new OkHttpClient();
            int updatesFound = 0;
            String lastUpdatedTitle = "";

            for (LibraryItem item : favorites) {
                Request request = new Request.Builder()
                        .url(item.getMangaId())
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                        .build();
                Response response = client.newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String html = response.body().string();
                    
                    // Regex for extracting chapters (based on MangaDetailActivity logic)
                    Pattern pattern = Pattern.compile("<li class=\"wp-manga-chapter[^\"]*\">.*?<a href=\"([^\"]+)\"[^>]*>(.*?)</a>", Pattern.DOTALL);
                    Matcher matcher = pattern.matcher(html);
                    if (matcher.find()) {
                        String latestChapterUrl = matcher.group(1).trim();
                        String latestChapterTitle = matcher.group(2).trim();
                        
                        // Check if this chapter is already cached/read
                        com.fire.mangareader.database.ChapterState state = db.chapterStateDao().getChapterState(latestChapterUrl);
                        if (state == null) {
                            updatesFound++;
                            lastUpdatedTitle = item.getTitle();
                        }
                    }
                }
            }

            if (updatesFound > 0) {
                showNotification(updatesFound, lastUpdatedTitle);
            }

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.retry();
        }
    }

    private void showNotification(int updatesFound, String lastUpdatedTitle) {
        String channelId = "manga_updates_channel";
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Manga Updates", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        String text = updatesFound == 1 ? "New chapter available for " + lastUpdatedTitle : updatesFound + " mangas have new chapters!";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(android.R.drawable.stat_notify_sync)
                .setContentTitle("New Manga Updates!")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(1001, builder.build());
    }
}
