package com.fire.mangareader.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MangaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LibraryItem item);

    @Update
    void update(LibraryItem item);

    @Delete
    void delete(LibraryItem item);

    @Query("SELECT * FROM library WHERE isFavorite = 1 ORDER BY addedTime DESC")
    LiveData<List<LibraryItem>> getFavorites();

    @Query("SELECT * FROM library WHERE isFavorite = 1 OR status IS NOT NULL ORDER BY addedTime DESC")
    LiveData<List<LibraryItem>> getAllLibraryItems();

    @Query("SELECT * FROM library WHERE isRead = 1 ORDER BY lastReadTime DESC")
    LiveData<List<LibraryItem>> getHistory();

    @Query("SELECT * FROM library WHERE mangaId = :mangaId LIMIT 1")
    LibraryItem getItemById(String mangaId);

    @Query("SELECT EXISTS(SELECT 1 FROM library WHERE mangaId = :mangaId AND isFavorite = 1)")
    boolean isFavorite(String mangaId);

    @Query("UPDATE library SET isFavorite = :favorite WHERE mangaId = :mangaId")
    void setFavorite(String mangaId, boolean favorite);

    @Query("UPDATE library SET isRead = 1, lastReadTime = :time, lastReadChapter = :chapter WHERE mangaId = :mangaId")
    void markAsRead(String mangaId, long time, String chapter);

    @Query("SELECT * FROM library WHERE isFavorite = 1")
    java.util.List<com.fire.mangareader.database.LibraryItem> getAllFavorites();

    @Query("SELECT * FROM library")
    java.util.List<com.fire.mangareader.database.LibraryItem> getAllItems();

    @Query("DELETE FROM library WHERE isFavorite = 0 AND isRead = 0")
    void cleanOrphans();
}
