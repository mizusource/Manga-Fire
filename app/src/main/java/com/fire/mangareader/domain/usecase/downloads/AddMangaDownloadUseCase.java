package com.fire.mangareader.domain.usecase.downloads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.data.database.DownloadedChapter;

public class AddMangaDownloadUseCase {
    private final Context context;

    public AddMangaDownloadUseCase(Context context) {
        this.context = context;
    }

    public interface Callback {
        void onSuccess();
        void onError(String error);
    }

    public void execute(DownloadedChapter chapter, Callback callback) {
        new Thread(() -> {
            try {
                AppDatabase.getInstance(context).downloadDao().insert(chapter);
                new Handler(Looper.getMainLooper()).post(callback::onSuccess);
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }
}
