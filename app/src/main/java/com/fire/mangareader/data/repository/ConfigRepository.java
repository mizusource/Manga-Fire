package com.fire.mangareader.data.repository;

import android.content.Context;
import com.fire.mangareader.data.database.AppDatabase;
import com.fire.mangareader.data.database.ConfigDao;
import com.fire.mangareader.data.database.ConfigEntity;
import com.google.gson.Gson;

public class ConfigRepository {
    private final ConfigDao configDao;
    private final Gson gson;

    public ConfigRepository(Context context) {
        this.configDao = AppDatabase.getInstance(context).configDao();
        this.gson = new Gson();
    }

    public void saveConfig(String key, String value) {
        new Thread(() -> {
            configDao.insert(new ConfigEntity(key, value));
        }).start();
    }

    public void saveObject(String key, Object object) {
        saveConfig(key, gson.toJson(object));
    }

    public String getConfigSync(String key) {
        return configDao.getConfig(key);
    }
}
