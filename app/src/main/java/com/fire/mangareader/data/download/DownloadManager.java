package com.fire.mangareader.data.download;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fire.mangareader.data.service.DownloadService;

import java.util.HashMap;
import java.util.Map;

public class DownloadManager {
    private final Context context;
    
    // Holds the progress of currently downloading chapters
    // Key: chapterUrl, Value: Integer progress (0-100)
    private final MutableLiveData<Map<String, Integer>> downloadProgress = new MutableLiveData<>(new HashMap<>());

    
    
    
    // ADDED FOR COMPOSE COMPATIBILITY
    public void enqueueDownload(String chapterId, String mangaId, String mangaTitle, String chapterTitle) {
        startDownload(mangaId, chapterId, chapterTitle);
    }

    public kotlinx.coroutines.flow.Flow<java.util.List<com.fire.mangareader.data.local.entity.DownloadedChapter>> getDownloadsFlow() {
        return kotlinx.coroutines.flow.FlowKt.emptyFlow();
    }
    
    public void deleteDownload(String chapterId) {
        cancelDownload(chapterId);
    }



    public DownloadManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void startDownload(String mangaUrl, String chapterUrl, String chapterTitle) {
        DownloadService.startDownload(context, mangaUrl, chapterUrl, chapterTitle);
    }

    public void cancelDownload(String chapterUrl) {
        // Find and cancel
        // For now, DownloadService only supports cancelAll
        DownloadService.cancelAllDownloads(context);
    }
    
    public void cancelAllDownloads() {
        DownloadService.cancelAllDownloads(context);
    }

    public void updateProgress(String chapterUrl, int progress) {
        Map<String, Integer> current = downloadProgress.getValue();
        if (current == null) current = new HashMap<>();
        current.put(chapterUrl, progress);
        downloadProgress.postValue(current);
    }
    
    public void removeProgress(String chapterUrl) {
        Map<String, Integer> current = downloadProgress.getValue();
        if (current != null && current.containsKey(chapterUrl)) {
            current.remove(chapterUrl);
            downloadProgress.postValue(current);
        }
    }

    public LiveData<Map<String, Integer>> getDownloadProgress() {
        return downloadProgress;
    }
}
