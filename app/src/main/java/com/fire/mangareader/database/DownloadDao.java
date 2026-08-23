package com.fire.mangareader.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DownloadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DownloadedChapter chapter);

    // جلب روابط الفصول المحملة لمانجا معينة (لتغيير لون زر التنزيل)
    @Query("SELECT chapterUrl FROM downloads WHERE mangaUrl = :mangaUrl")
    List<String> getDownloadedChapterUrls(String mangaUrl);

    @Query("DELETE FROM downloads")
    void deleteAll();
    
    // جلب مسار الصور عند القراءة بدون إنترنت
    @Query("SELECT * FROM downloads WHERE chapterUrl = :chapterUrl LIMIT 1")
    DownloadedChapter getDownloadInfo(String chapterUrl);

    // جلب جميع التنزيلات لعرضها في الشاشة
    @Query("SELECT * FROM downloads")
    List<DownloadedChapter> getAllDownloads();

    // حذف التنزيل من قاعدة البيانات (كائن)
    @androidx.room.Delete
    void delete(DownloadedChapter chapter);

    // 🚀 دالة الحذف السريع برابط الفصل
    @Query("DELETE FROM downloads WHERE chapterUrl = :url")
    void deleteByUrl(String url);
}
