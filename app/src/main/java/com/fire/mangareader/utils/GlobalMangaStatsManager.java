package com.fire.mangareader.utils;

public class GlobalMangaStatsManager {
    public interface RatingCallback {
        void onSuccess(double newAverage, int totalVotes);
        void onError(String error);
    }
    public interface StatsCallback {
        void onSuccess(GlobalMangaStats stats);
        void onError(String error);
    }

    public static void submitRating(String mangaUrl, String mangaTitle, float overall, float story, float characters, float art, RatingCallback callback) {
        if (callback != null) callback.onSuccess(overall, 1);
    }

    public static void fetchStats(String mangaUrl, StatsCallback callback) {
        if (callback != null) {
            callback.onSuccess(new GlobalMangaStats(8.9, 8.7, 9.1, 9.3, 45));
        }
    }
}
