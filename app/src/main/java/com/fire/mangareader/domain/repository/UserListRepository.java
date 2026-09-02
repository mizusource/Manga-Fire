package com.fire.mangareader.domain.repository;

import com.fire.mangareader.data.database.LibraryItem;
import com.fire.mangareader.data.database.ChapterState;
import java.util.List;

public interface UserListRepository {
    /**
     * Add a manga to the user's library with a specific status (reading, completed, plan_to_read, favorite).
     */
    void addToLibrary(String mangaUrl, String title, String coverUrl, String status);

    /**
     * Remove a manga from the user's library.
     */
    void removeFromLibrary(String mangaUrl);

    /**
     * Get all items in the user's library, optionally filtered by status.
     */
    List<LibraryItem> getLibraryItems(String status);

    /**
     * Mark a specific chapter as read.
     */
    void markChapterAsRead(String mangaUrl, String chapterUrl, boolean isRead);

    /**
     * Retrieve the reading states for a specific manga.
     */
    List<ChapterState> getMangaChapterStates(String mangaUrl);
}
