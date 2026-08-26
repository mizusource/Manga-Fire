package com.fire.mangareader.network;

import android.content.Context;
import android.content.SharedPreferences;

public class SourceManager {
    public static final String SOURCE_MANGALIK = "https://mangalik.net/";
    public static final String SOURCE_MANGA_STARZ = "https://manga-starz.net/";
    public static final String SOURCE_LEKMANGA = "https://lekmanga.net/";
    public static final String SOURCE_SWATMANGA = "https://swatmanga.co/";
    public static final String SOURCE_MANGAPRO = "https://mangapro.me/";

    public static String[] getAllSources() {
        return new String[]{
            SOURCE_MANGALIK,
            SOURCE_MANGA_STARZ,
            SOURCE_LEKMANGA,
            SOURCE_SWATMANGA,
            SOURCE_MANGAPRO
        };
    }

    public static String getActiveSource(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("manga_prefs", Context.MODE_PRIVATE);
        return prefs.getString("active_source", SOURCE_MANGALIK);
    }
    
    public static String getActiveSourceName(Context context) {
        String url = getActiveSource(context);
        if (url.equals(SOURCE_MANGA_STARZ)) return "Manga-Starz";
        if (url.equals(SOURCE_LEKMANGA)) return "LekManga";
        if (url.equals(SOURCE_SWATMANGA)) return "SwatManga";
        if (url.equals(SOURCE_MANGAPRO)) return "MangaPro";
        return "Manga Lik";
    }

    public static void setActiveSource(Context context, String sourceUrl) {
        SharedPreferences prefs = context.getSharedPreferences("manga_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("active_source", sourceUrl).apply();
    }
}

