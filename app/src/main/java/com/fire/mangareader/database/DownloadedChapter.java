package com.fire.mangareader.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "downloads")
public class DownloadedChapter {
    @PrimaryKey
    @NonNull
    public String chapterUrl = "";
    public String mangaUrl;
    public String chapterTitle;
    public String localFolderPath; // مسار المجلد الذي يحتوي على الصور المحملة
}
