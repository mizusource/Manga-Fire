package com.fire.mangareader.data.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chapter_state")
public class ChapterState {
    @PrimaryKey
    @NonNull
    public String chapterUrl = "";
    public String mangaUrl;
    public boolean isRead;
    public boolean isCompleted; // هل قرأ كل الصفحات؟
    public int lastPage; // رقم آخر صفحة وصل إليها
}
