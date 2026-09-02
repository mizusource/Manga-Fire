package com.fire.mangareader.util;

import android.content.Context;
import android.net.Uri;
import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.data.database.ChapterState;
import com.fire.mangareader.data.database.LibraryItem;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class BackupManager {

    public interface BackupCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public static void exportBackup(Context context, Uri destinationUri, BackupCallback callback) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                List<LibraryItem> favorites = db.mangaDao().getAllFavorites();
                List<ChapterState> readStates = db.chapterStateDao().getAllReadStates();
                List<RecentReadingManager.RecentItem> recentItems = RecentReadingManager.getRecent(context);

                JSONObject root = new JSONObject();
                root.put("version", 1);
                root.put("timestamp", System.currentTimeMillis());
                root.put("appName", "MangaReader");

                // Favorites
                JSONArray favArray = new JSONArray();
                if (favorites != null) {
                    for (LibraryItem item : favorites) {
                        JSONObject obj = new JSONObject();
                        obj.put("mangaId", item.getMangaId());
                        obj.put("title", item.getTitle());
                        obj.put("coverUrl", item.getCoverUrl());
                        obj.put("status", item.getStatus());
                        obj.put("isFavorite", item.isFavorite());
                        obj.put("isRead", item.isRead());
                        obj.put("lastReadTime", item.getLastReadTime());
                        obj.put("lastReadChapter", item.getLastReadChapter());
                        obj.put("addedTime", item.getAddedTime());
                        favArray.put(obj);
                    }
                }
                root.put("favorites", favArray);

                // Reading Progress / Chapter States
                JSONArray stateArray = new JSONArray();
                if (readStates != null) {
                    for (ChapterState state : readStates) {
                        JSONObject obj = new JSONObject();
                        obj.put("chapterUrl", state.chapterUrl);
                        obj.put("mangaUrl", state.mangaUrl);
                        obj.put("lastPage", state.lastPage);
                        obj.put("isRead", state.isRead);
                        obj.put("isCompleted", state.isCompleted);
                        stateArray.put(obj);
                    }
                }
                root.put("chapterStates", stateArray);

                // Recent items
                JSONArray recentArray = new JSONArray();
                if (recentItems != null) {
                    for (RecentReadingManager.RecentItem r : recentItems) {
                        JSONObject obj = new JSONObject();
                        obj.put("mangaUrl", r.mangaUrl);
                        obj.put("mangaTitle", r.mangaTitle);
                        obj.put("mangaCover", r.mangaCover);
                        obj.put("chapterUrl", r.chapterUrl);
                        obj.put("chapterTitle", r.chapterTitle);
                        recentArray.put(obj);
                    }
                }
                root.put("recent", recentArray);

                String jsonString = root.toString(2);
                OutputStream os = context.getContentResolver().openOutputStream(destinationUri);
                if (os != null) {
                    os.write(jsonString.getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        callback.onSuccess("تم تصدير النسخة الاحتياطية بنجاح! (" + favArray.length() + " مانجا مفضلة)");
                    });
                } else {
                    throw new Exception("فشل في فتح مسار الحفظ");
                }
            } catch (Exception e) {
                e.printStackTrace();
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError("فشل التصدير: " + e.getMessage());
                });
            }
        }).start();
    }

    public static void importBackup(Context context, Uri sourceUri, BackupCallback callback) {
        new Thread(() -> {
            try {
                InputStream is = context.getContentResolver().openInputStream(sourceUri);
                if (is == null) throw new Exception("تعذر فتح ملف النسخة الاحتياطية");

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                reader.close();
                is.close();

                JSONObject root = new JSONObject(sb.toString());
                AppDatabase db = AppDatabase.getInstance(context);

                int restoredFavorites = 0;
                int restoredStates = 0;

                // Import Favorites
                if (root.has("favorites")) {
                    JSONArray favArray = root.getJSONArray("favorites");
                    for (int i = 0; i < favArray.length(); i++) {
                        JSONObject obj = favArray.getJSONObject(i);
                        LibraryItem item = new LibraryItem();
                        item.setMangaId(obj.optString("mangaId"));
                        item.setTitle(obj.optString("title"));
                        item.setCoverUrl(obj.optString("coverUrl"));
                        item.setStatus(obj.optString("status"));
                        item.setFavorite(obj.optBoolean("isFavorite", true));
                        item.setRead(obj.optBoolean("isRead", false));
                        item.setLastReadTime(obj.optLong("lastReadTime", System.currentTimeMillis()));
                        item.setLastReadChapter(obj.optString("lastReadChapter"));
                        item.setAddedTime(obj.optLong("addedTime", System.currentTimeMillis()));
                        
                        if (item.getMangaId() != null && !item.getMangaId().isEmpty()) {
                            db.mangaDao().insert(item);
                            restoredFavorites++;
                        }
                    }
                }

                // Import Chapter States
                if (root.has("chapterStates")) {
                    JSONArray stateArray = root.getJSONArray("chapterStates");
                    for (int i = 0; i < stateArray.length(); i++) {
                        JSONObject obj = stateArray.getJSONObject(i);
                        ChapterState state = new ChapterState();
                        state.chapterUrl = obj.optString("chapterUrl");
                        state.mangaUrl = obj.optString("mangaUrl");
                        state.lastPage = obj.optInt("lastPage", 0);
                        state.isRead = obj.optBoolean("isRead", true);
                        state.isCompleted = obj.optBoolean("isCompleted", false);

                        if (state.chapterUrl != null && !state.chapterUrl.isEmpty()) {
                            db.chapterStateDao().insert(state);
                            restoredStates++;
                        }
                    }
                }

                // Import Recent
                if (root.has("recent")) {
                    JSONArray recentArray = root.getJSONArray("recent");
                    for (int i = 0; i < recentArray.length(); i++) {
                        JSONObject obj = recentArray.getJSONObject(i);
                        RecentReadingManager.RecentItem r = new RecentReadingManager.RecentItem();
                        r.mangaUrl = obj.optString("mangaUrl");
                        r.mangaTitle = obj.optString("mangaTitle");
                        r.mangaCover = obj.optString("mangaCover");
                        r.chapterUrl = obj.optString("chapterUrl");
                        r.chapterTitle = obj.optString("chapterTitle");
                        RecentReadingManager.addRecent(context, r);
                    }
                }

                int totalRestored = restoredFavorites;
                int totalStates = restoredStates;
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onSuccess("تم استعادة النسخة الاحتياطية بنجاح! (" + totalRestored + " مانجا، " + totalStates + " فصول)");
                });
            } catch (Exception e) {
                e.printStackTrace();
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError("فشل الاستعادة: ملف غير صالح أو تالف (" + e.getMessage() + ")");
                });
            }
        }).start();
    }
}
