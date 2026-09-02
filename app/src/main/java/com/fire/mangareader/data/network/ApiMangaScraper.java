package com.fire.mangareader.data.network;

import android.os.Handler;
import android.os.Looper;

import com.fire.mangareader.util.MangaOkHttp;
import com.fire.mangareader.data.network.MangaScraper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class ApiMangaScraper {
    private static final Gson gson = new Gson();

    // ==========================================
    // 1. DTO (Data Transfer Objects)
    // ==========================================
    public static class DilarChapterDto {
        @SerializedName("releases")
        public List<Release> releases;
    }

    public static class Release {
        @SerializedName("pages")
        public List<String> pages;
        @SerializedName("storage_key")
        public String storageKey;
    }

    // ==========================================
    // 2. Fast API Fetching using OkHttp & Gson
    // ==========================================
    public static void fetchChapterPagesFast(String apiUrl, MangaScraper.ChapterPagesCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(apiUrl)
                        .header("Accept", "application/json")
                        .build();

                Response response = MangaOkHttp.getClient().newCall(request).execute();
                
                if (!response.isSuccessful() || response.body() == null) {
                    throw new Exception("HTTP Error: " + response.code());
                }

                String jsonResponse = response.body().string();
                
                DilarChapterDto dto = gson.fromJson(jsonResponse, DilarChapterDto.class);
                
                List<String> imageUrls = new ArrayList<>();
                if (dto != null && dto.releases != null && !dto.releases.isEmpty()) {
                    Release release = dto.releases.get(0);
                    if (release.pages != null) {
                        for (String pageUrl : release.pages) {
                            imageUrls.add(pageUrl); 
                        }
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!imageUrls.isEmpty()) {
                        callback.onSuccess(imageUrls);
                    } else {
                        callback.onError("لم يتم العثور على صفحات في استجابة الـ API.");
                    }
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("خطأ في الاتصال بالـ API: " + e.getMessage()));
            }
        }).start();
    }
}
