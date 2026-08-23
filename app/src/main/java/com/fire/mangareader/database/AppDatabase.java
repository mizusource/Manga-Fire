package com.fire.mangareader.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// تم إضافة الجداول الجديدة ورفع الإصدار إلى 5
@Database(entities = {
        LibraryItem.class, 
        DownloadedChapter.class, 
        ChapterState.class, 
        CachedManga.class, 
        CachedChapter.class
}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract MangaDao mangaDao();
    public abstract DownloadDao downloadDao();
    public abstract ChapterStateDao chapterStateDao();
    public abstract CacheDao cacheDao(); // تم إضافة الـ DAO الخاص بالتخزين الذكي

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "manga_fire_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
