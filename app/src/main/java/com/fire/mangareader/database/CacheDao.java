package com.fire.mangareader.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertManga(CachedManga manga);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChapters(List<CachedChapter> chapters);

    @Query("SELECT * FROM cached_manga WHERE mangaUrl = :url LIMIT 1")
    CachedManga getMangaDetails(String url);

    // جلب الفصول مرتبة كما تم حفظها
    @Query("SELECT * FROM cached_chapter WHERE mangaUrl = :url ORDER BY chapterOrder ASC")
    List<CachedChapter> getMangaChapters(String url);

    // مسح الفصول القديمة عند عمل تحديث (Refresh)
    @Query("DELETE FROM cached_chapter WHERE mangaUrl = :url")
    void deleteChaptersForManga(String url);

    // دالة مجمعة لحفظ المانهوا وفصولها معاً
    @Transaction
    default void cacheMangaAndChapters(CachedManga manga, List<CachedChapter> chapters) {
        insertManga(manga);
        deleteChaptersForManga(manga.mangaUrl);
        insertChapters(chapters);
    }
}
