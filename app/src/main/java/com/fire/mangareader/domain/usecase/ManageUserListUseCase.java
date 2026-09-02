package com.fire.mangareader.domain.usecase;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.data.database.LibraryItem;
import com.fire.mangareader.domain.model.Manga;

public class ManageUserListUseCase {

    private final Context context;

    public ManageUserListUseCase(Context context) {
        this.context = context;
    }

    public interface ActionCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    /**
     * Submit manga to a specific list (favorite, reading, watched, will_watch, dropped).
     */
    public void submitMangaToUserList(Manga manga, String listType, ActionCallback callback) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                LibraryItem existingItem = db.mangaDao().getItemById(manga.getUrl());

                if (existingItem == null) {
                    existingItem = new LibraryItem();
                    existingItem.setMangaId(manga.getUrl());
                    existingItem.setTitle(manga.getTitle());
                    existingItem.setCoverUrl(manga.getCoverUrl());
                    existingItem.setAddedTime(System.currentTimeMillis());
                }

                existingItem.setStatus(listType);
                existingItem.setFavorite("favorite".equals(listType));

                db.mangaDao().insert(existingItem);
                
                String displayStatus = mapStatusToDisplay(listType);
                postSuccess(callback, "تم إضافة المانجا إلى قائمة: " + displayStatus);

            } catch (Exception e) {
                postError(callback, "فشل في تحديث حالة المانجا: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Delete manga from the user library and optionally clear downloaded chapters.
     */
    public void deleteFromUserList(String mangaUrl, boolean deleteDownloads, ActionCallback callback) {
        new Thread(() -> {
            try {
                AppDatabase db = AppDatabase.getInstance(context);
                LibraryItem item = db.mangaDao().getItemById(mangaUrl);
                
                if (item != null) {
                    db.mangaDao().delete(item);
                }

                if (deleteDownloads) {
                    java.io.File mangaFolder = new java.io.File(context.getFilesDir(), String.valueOf(mangaUrl.hashCode()));
                    if (mangaFolder.exists() && mangaFolder.isDirectory()) {
                        deleteRecursive(mangaFolder);
                    }
                }

                postSuccess(callback, "تمت الإزالة بنجاح");
            } catch (Exception e) {
                postError(callback, "فشل الحذف: " + e.getMessage());
            }
        }).start();
    }

    private void deleteRecursive(java.io.File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            java.io.File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (java.io.File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        fileOrDirectory.delete();
    }

    private String mapStatusToDisplay(String status) {
        switch (status) {
            case "favorite": return "المفضلة ❤️";
            case "reading": return "أقرأها حالياً 📖";
            case "watched": return "مكتملة ✅";
            case "will_watch": return "مخطط قراءتها 🗓️";
            case "dropped": return "تم تركها ❌";
            default: return status;
        }
    }

    private void postSuccess(ActionCallback callback, String message) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(message));
    }

    private void postError(ActionCallback callback, String error) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
    }
}
