package com.fire.mangareader.domain.usecase.downloads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.data.database.DownloadedChapter;

import java.io.File;

public class DeleteMangaDownloadUseCase {
    private final Context context;

    public DeleteMangaDownloadUseCase(Context context) {
        this.context = context;
    }

    public interface Callback {
        void onSuccess();
        void onError(String error);
    }

    public void execute(String chapterUrl, Callback callback) {
        new Thread(() -> {
            try {
                DownloadedChapter chapter = AppDatabase.getInstance(context).downloadDao().getDownloadInfo(chapterUrl);
                if (chapter != null) {
                    AppDatabase.getInstance(context).downloadDao().deleteByUrl(chapterUrl);
                    if (chapter.localFolderPath != null) {
                        File folder = new File(chapter.localFolderPath);
                        if (folder.exists() && folder.isDirectory()) {
                            deleteRecursive(folder);
                        }
                    }
                }
                new Handler(Looper.getMainLooper()).post(callback::onSuccess);
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        fileOrDirectory.delete();
    }
}
