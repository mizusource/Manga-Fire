package com.fire.mangareader.utils;

import android.content.Context;
import com.fire.mangareader.network.SupabaseManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class GlobalMangaStatsManager {

    public interface RatingCallback {
        void onSuccess(double newAverage, int totalVotes);
        void onError(String error);
    }

    public interface StatsCallback {
        void onSuccess(GlobalMangaStats stats);
        void onError(String error);
    }

    public static void submitRating(Context context, String mangaUrl, String mangaTitle, float overall, float story, float characters, float art, RatingCallback callback) {
        SupabaseManager.getInstance(context).submitRating(mangaUrl, overall, story, characters, art, new SupabaseManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                // If saved successfully, fetch new stats to get the average
                fetchStats(context, mangaUrl, new StatsCallback() {
                    @Override
                    public void onSuccess(GlobalMangaStats stats) {
                        if (callback != null) callback.onSuccess(stats.overallAverage, stats.totalVotes);
                    }
                    @Override
                    public void onError(String error) {
                        if (callback != null) callback.onError(error);
                    }
                });
            }
            @Override
            public void onError(String error) {
                if (callback != null) callback.onError(error);
            }
        });
    }

    public static void fetchStats(Context context, String mangaUrl, StatsCallback callback) {
        SupabaseManager.getInstance(context).getMangaStats(mangaUrl, new SupabaseManager.DataCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                if (data == null || data.length() == 0) {
                    if (callback != null) callback.onSuccess(new GlobalMangaStats(0,0,0,0,0));
                    return;
                }
                
                double sumOverall = 0, sumStory = 0, sumChars = 0, sumArt = 0;
                int count = data.length();
                
                try {
                    for (int i = 0; i < count; i++) {
                        JSONObject row = data.getJSONObject(i);
                        sumOverall += row.optDouble("overall", 0);
                        sumStory += row.optDouble("story", 0);
                        sumChars += row.optDouble("characters", 0);
                        sumArt += row.optDouble("art", 0);
                    }
                } catch (Exception e) {}
                
                if (callback != null) {
                    callback.onSuccess(new GlobalMangaStats(
                        count > 0 ? sumOverall / count : 0,
                        count > 0 ? sumStory / count : 0,
                        count > 0 ? sumChars / count : 0,
                        count > 0 ? sumArt / count : 0,
                        count
                    ));
                }
            }

            @Override
            public void onError(String error) {
                if (callback != null) callback.onError(error);
            }
        });
    }
}
