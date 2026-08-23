package com.fire.mangareader.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "library")
public class LibraryItem {
    @PrimaryKey
    @NonNull
    private String mangaId;
    private String title;
    private String coverUrl;
    private String status;
    private boolean isFavorite;
    private boolean isRead;
    private long lastReadTime;
    private String lastReadChapter;
    private long addedTime;

    public String getMangaId() { return mangaId; }
    public void setMangaId(String mangaId) { this.mangaId = mangaId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public long getLastReadTime() { return lastReadTime; }
    public void setLastReadTime(long lastReadTime) { this.lastReadTime = lastReadTime; }
    public String getLastReadChapter() { return lastReadChapter; }
    public void setLastReadChapter(String lastReadChapter) { this.lastReadChapter = lastReadChapter; }
    public long getAddedTime() { return addedTime; }
    public void setAddedTime(long addedTime) { this.addedTime = addedTime; }
}
