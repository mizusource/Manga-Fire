package com.fire.mangareader.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ChapterStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ChapterState state);

    // جلب جميع الحالات لمانجا معينة
    @Query("SELECT * FROM chapter_state WHERE mangaUrl = :mangaUrl")
    List<ChapterState> getAllStatesForManga(String mangaUrl);

    // جلب حالة فصل واحد فقط (لشاشة القراءة)
    @Query("SELECT * FROM chapter_state WHERE chapterUrl = :chapterUrl LIMIT 1")
    ChapterState getChapterState(String chapterUrl);
    
    // دالة جلب جميع الفصول التي تم قراءتها (تم تصحيح اسم الجدول هنا)
    @Query("SELECT * FROM chapter_state WHERE isRead = 1")
    List<ChapterState> getAllReadStates();
}
