package com.fire.mangareader.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class PreferencesHelper {
    private final SharedPreferences prefs;

    public PreferencesHelper(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public void saveString(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public void saveBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }
}
