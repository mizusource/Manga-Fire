package com.fire.mangareader.network;

import android.content.Context;
import android.content.SharedPreferences;

public class SourceManager {
    public static final String SOURCE_MANGALIK = "https://mangalik.net/";
    public static final String SOURCE_MANGA_STARZ = "https://manga-starz.net/";
    public static final String SOURCE_MANGATEK = "https://mangatek.com/";
    public static final String SOURCE_MANGASID = "https://mangasid.com/";

    public static String getActiveSource(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("manga_prefs", Context.MODE_PRIVATE);
        return prefs.getString("active_source", SOURCE_MANGALIK);
    }
    
    public static String getActiveSourceName(Context context) {
        String url = getActiveSource(context);
        if (url.equals(SOURCE_MANGA_STARZ)) return "Manga-Starz";
        if (url.equals(SOURCE_MANGATEK)) return "Mangatek";
        if (url.equals(SOURCE_MANGASID)) return "Mangasid";
        return "Manga Lik";
    }

    public static void setActiveSource(Context context, String sourceUrl) {
        SharedPreferences prefs = context.getSharedPreferences("manga_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("active_source", sourceUrl).apply();
    }
}
