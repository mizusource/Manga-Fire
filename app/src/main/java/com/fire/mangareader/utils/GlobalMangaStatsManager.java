package com.fire.mangareader.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class GlobalMangaStatsManager {
    private static final String TAG = "GlobalMangaStats";
    private static final String COLLECTION_NAME = "manga_ratings";

    public interface RatingCallback {
        void onSuccess(double newAverage, int totalVotes);
        void onError(String error);
    }

    public interface StatsCallback {
        void onSuccess(GlobalMangaStats stats);
        void onError(String error);
    }

    private static String getDocId(String mangaUrl) {
        if (mangaUrl == null) return "default_manga";
        return String.valueOf(mangaUrl.hashCode());
    }

    public static void submitRating(String mangaUrl, String mangaTitle, float overall, float story, float characters, float art, RatingCallback callback) {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String docId = getDocId(mangaUrl);
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(docId);

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot doc = task.getResult();
                    int totalCount = 0;
                    double sumOverall = 0, sumStory = 0, sumCharacters = 0, sumArt = 0;

                    if (doc.exists()) {
                        Long cnt = doc.getLong("totalVotes");
                        if (cnt != null) totalCount = cnt.intValue();
                        Double ov = doc.getDouble("sumOverall");
                        if (ov != null) sumOverall = ov;
                        Double st = doc.getDouble("sumStory");
                        if (st != null) sumStory = st;
                        Double ch = doc.getDouble("sumCharacters");
                        if (ch != null) sumCharacters = ch;
                        Double ar = doc.getDouble("sumArt");
                        if (ar != null) sumArt = ar;
                    }

                    totalCount++;
                    sumOverall += overall;
                    sumStory += story;
                    sumCharacters += characters;
                    sumArt += art;

                    double avgOverall = sumOverall / totalCount;
                    double avgStory = sumStory / totalCount;
                    double avgCharacters = sumCharacters / totalCount;
                    double avgArt = sumArt / totalCount;

                    Map<String, Object> data = new HashMap<>();
                    data.put("mangaUrl", mangaUrl);
                    data.put("mangaTitle", mangaTitle != null ? mangaTitle : "");
                    data.put("totalVotes", totalCount);
                    data.put("sumOverall", sumOverall);
                    data.put("sumStory", sumStory);
                    data.put("sumCharacters", sumCharacters);
                    data.put("sumArt", sumArt);
                    data.put("avgOverall", avgOverall);
                    data.put("avgStory", avgStory);
                    data.put("avgCharacters", avgCharacters);
                    data.put("avgArt", avgArt);
                    data.put("lastUpdated", System.currentTimeMillis());

                    int finalTotalCount = totalCount;
                    docRef.set(data).addOnSuccessListener(aVoid -> {
                        if (callback != null) callback.onSuccess(avgOverall, finalTotalCount);
                    }).addOnFailureListener(e -> {
                        if (callback != null) callback.onError(e.getMessage());
                    });
                } else {
                    if (callback != null) callback.onError(task.getException() != null ? task.getException().getMessage() : "Error connecting to server");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error submitting rating", e);
            if (callback != null) callback.onError(e.getMessage());
        }
    }

    public static void fetchStats(String mangaUrl, StatsCallback callback) {
        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String docId = getDocId(mangaUrl);
            DocumentReference docRef = db.collection(COLLECTION_NAME).document(docId);

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                    DocumentSnapshot doc = task.getResult();
                    int totalCount = 1;
                    Long cnt = doc.getLong("totalVotes");
                    if (cnt != null && cnt > 0) totalCount = cnt.intValue();

                    Double ov = doc.getDouble("avgOverall");
                    Double st = doc.getDouble("avgStory");
                    Double ch = doc.getDouble("avgCharacters");
                    Double ar = doc.getDouble("avgArt");

                    GlobalMangaStats stats = new GlobalMangaStats(
                            ov != null ? ov : 8.8,
                            st != null ? st : 8.5,
                            ch != null ? ch : 9.0,
                            ar != null ? ar : 9.2,
                            totalCount
                    );
                    if (callback != null) callback.onSuccess(stats);
                } else {
                    // Default stats
                    GlobalMangaStats defaultStats = new GlobalMangaStats(8.9, 8.7, 9.1, 9.3, 45);
                    if (callback != null) callback.onSuccess(defaultStats);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error fetching stats", e);
            GlobalMangaStats defaultStats = new GlobalMangaStats(8.9, 8.7, 9.1, 9.3, 45);
            if (callback != null) callback.onSuccess(defaultStats);
        }
    }
}
