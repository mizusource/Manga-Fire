package com.fire.mangareader.domain.usecase;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.data.database.LibraryItem;
import com.fire.mangareader.domain.model.Chapter;
import com.fire.mangareader.domain.model.Manga;
import com.fire.mangareader.util.DownloadChecker;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GetMangaDetailUseCase {

    private final Context context;

    public GetMangaDetailUseCase(Context context) {
        this.context = context;
    }

    public interface MangaDetailCallback {
        void onSuccess(Manga manga, List<Chapter> chapters);
        void onError(String error);
    }

    /**
     * Executes the use case to fetch manga details.
     * @param url The manga URL.
     * @param isOffline True if fetching local data only.
     * @param callback Result callback.
     */
    public void execute(String url, boolean isOffline, MangaDetailCallback callback) {
        if (isOffline) {
            fetchOfflineDetails(url, callback);
        } else {
            // Note: In a complete Clean Architecture, this would call MangaRepository.getMangaDetails()
            // For now, this serves as the entry point that UI can call to indicate online intention.
            // Since online scraping involves WebView injection currently in MangaDetailActivity,
            // we pass an error here to tell the UI to fallback to its WebView logic or we can migrate it.
            callback.onError("ONLINE_FETCH_REQUIRED");
        }
    }

    private void fetchOfflineDetails(String url, MangaDetailCallback callback) {
        new Thread(() -> {
            try {
                // 1. Get Details from local Library (Room DB)
                LibraryItem item = AppDatabase.getInstance(context).mangaDao().getItemById(url);
                if (item == null) {
                    postError(callback, "هذه المانجا غير محفوظة في المكتبة. يرجى الاتصال بالإنترنت.");
                    return;
                }

                Manga manga = new Manga();
                manga.setTitle(item.getTitle());
                manga.setUrl(item.getMangaId());
                manga.setCoverUrl(item.getCoverUrl());
                manga.setStoryStatus(item.getStatus());

                // 2. Scan for downloaded chapters from local storage
                List<Chapter> downloadedChapters = new ArrayList<>();
                File mangaFolder = new File(context.getFilesDir(), String.valueOf(url.hashCode()));
                
                if (mangaFolder.exists() && mangaFolder.isDirectory()) {
                    File[] chapterFolders = mangaFolder.listFiles();
                    if (chapterFolders != null) {
                        // Sort folders so they appear in a consistent order
                        Arrays.sort(chapterFolders, (f1, f2) -> f2.getName().compareTo(f1.getName()));
                        
                        for (File cf : chapterFolders) {
                            if (cf.isDirectory()) {
                                // Reconstruct Chapter from offline folder
                                Chapter ch = new Chapter();
                                // We store the chapter URL hash as the folder name.
                                // We cannot easily get the original URL or Title without a DB table for chapters.
                                // But we can provide enough data for ChapterReaderActivity to load from offline.
                                ch.setUrl(cf.getName()); // Pass the folder name as URL reference
                                ch.setTitle("فصل محمل (" + cf.getName().substring(0, Math.min(6, cf.getName().length())) + ")");
                                downloadedChapters.add(ch);
                            }
                        }
                    }
                }

                if (downloadedChapters.isEmpty()) {
                    postError(callback, "لا توجد فصول محملة لهذه المانجا.");
                } else {
                    postSuccess(callback, manga, downloadedChapters);
                }

            } catch (Exception e) {
                postError(callback, "حدث خطأ أثناء جلب البيانات المحلية: " + e.getMessage());
            }
        }).start();
    }

    private void postSuccess(MangaDetailCallback callback, Manga manga, List<Chapter> chapters) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(manga, chapters));
    }

    private void postError(MangaDetailCallback callback, String error) {
        new Handler(Looper.getMainLooper()).post(() -> callback.onError(error));
    }
}
