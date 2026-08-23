package com.fire.mangareader.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_chapter")
public class CachedChapter {
    @PrimaryKey
    @NonNull
    public String chapterUrl = "";
    public String mangaUrl;
    public String title;
    public int chapterOrder; // للحفاظ على ترتيب الفصول الأصلي
}
