package com.fire.mangareader.data.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "config_data")
public class ConfigEntity {
    @PrimaryKey
    @NonNull
    public String type;

    public String data;

    public ConfigEntity(@NonNull String type, String data) {
        this.type = type;
        this.data = data;
    }
}
