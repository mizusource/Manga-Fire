package com.fire.mangareader.domain.repository;

import java.io.File;
import java.util.List;

public interface UserDownloadsRepository {
    /**
     * Get a list of downloaded chapter directories for a specific manga.
     */
    List<File> getDownloadedChapters(String mangaUrl);

    /**
     * Check if a specific chapter is downloaded and available offline.
     */
    boolean isChapterDownloaded(String mangaUrl, String chapterUrl);

    /**
     * Delete a downloaded chapter to free up space.
     */
    boolean deleteDownloadedChapter(String mangaUrl, String chapterUrl);

    /**
     * Get the total size of all downloaded files.
     */
    long getTotalDownloadsSize();
}
