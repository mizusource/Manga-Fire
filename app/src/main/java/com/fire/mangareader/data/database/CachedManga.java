package com.fire.mangareader.data.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_manga")
public class CachedManga {
    @PrimaryKey
    @NonNull
    public String mangaUrl = "";
    public String description;
    public String status;
}
