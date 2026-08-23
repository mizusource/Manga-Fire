package com.fire.mangareader.utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecentReadingManager {
    private static final String PREFS_NAME = "recent_reading_prefs";
    private static final String KEY_RECENT = "recent_items";

    public static class RecentItem {
        public String mangaUrl;
        public String mangaTitle;
        public String mangaCover;
        public String chapterUrl;
        public String chapterTitle;
    }

    public static void addRecent(Context context, RecentItem item) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_RECENT, "[]");
        try {
            JSONArray array = new JSONArray(json);
            JSONArray newArray = new JSONArray();
            JSONObject newItem = new JSONObject();
            newItem.put("mangaUrl", item.mangaUrl);
            newItem.put("mangaTitle", item.mangaTitle);
            newItem.put("mangaCover", item.mangaCover);
            newItem.put("chapterUrl", item.chapterUrl);
            newItem.put("chapterTitle", item.chapterTitle);
            newArray.put(newItem);

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                if (!obj.getString("mangaUrl").equals(item.mangaUrl) && newArray.length() < 10) {
                    newArray.put(obj);
                }
            }
            prefs.edit().putString(KEY_RECENT, newArray.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<RecentItem> getRecent(Context context) {
        List<RecentItem> list = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_RECENT, "[]");
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                RecentItem item = new RecentItem();
                item.mangaUrl = obj.getString("mangaUrl");
                item.mangaTitle = obj.optString("mangaTitle", "");
                item.mangaCover = obj.optString("mangaCover", "");
                item.chapterUrl = obj.optString("chapterUrl", "");
                item.chapterTitle = obj.optString("chapterTitle", "");
                list.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
