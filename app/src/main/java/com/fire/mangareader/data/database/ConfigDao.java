package com.fire.mangareader.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ConfigEntity config);

    @Query("SELECT data FROM config_data WHERE type = :type LIMIT 1")
    String getConfig(String type);

    @Query("DELETE FROM config_data WHERE type = :type")
    void deleteConfig(String type);
}
