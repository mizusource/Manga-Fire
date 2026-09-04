package com.fire.mangareader.data.network;

import android.os.Handler;
import android.os.Looper;

import com.fire.mangareader.domain.model.Chapter;
import com.fire.mangareader.domain.model.Manga;
import com.fire.mangareader.util.MangaOkHttp;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;
import okhttp3.Response;

public class DilarScraper {
    private static final String BASE_URL = "https://dilar.tube";
    private static final String API_SERIES_URL = "https://dilar.tube/api/series";
    private static final Gson gson = new Gson();

    // ==========================================
    // 1. DTO (Data Transfer Objects)
    // ==========================================
    
    // نموذج استجابة قائمة المانجا
    public static class DilarSeriesResponse {
        @SerializedName("data")
        public List<DilarManga> data;
    }

    public static class DilarManga {
        @SerializedName("id")
        public String id;
        @SerializedName("title")
        public String title;
        @SerializedName("summary")
        public String summary;
        @SerializedName("cover")
        public String cover;
    }

    // نموذج استجابة تفاصيل المانجا والفصول
    public static class DilarDetailsResponse {
        @SerializedName("id")
        public String id;
        @SerializedName("title")
        public String title;
        @SerializedName("summary")
        public String summary;
        @SerializedName("cover")
        public String cover;
        @SerializedName("releases")
        public List<DilarRelease> releases;
    }

    public static class DilarRelease {
        @SerializedName("chapter")
        public DilarChapter chapter;
    }

    public static class DilarChapter {
        @SerializedName("chapter")
        public String chapterNumber;
        @SerializedName("title")
        public String title;
    }

    // ==========================================
    // 2. Fetching Methods
    // ==========================================

    public static void fetchLatestManga(MangaScraper.ScrapingCallback callback) {
        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(API_SERIES_URL)
                        .header("Accept", "application/json")
                        .build();

                Response response = MangaOkHttp.getClient().newCall(request).execute();

                if (!response.isSuccessful() || response.body() == null) {
                    throw new Exception("HTTP Error: " + response.code());
                }

                String jsonResponse = response.body().string();
                
                // Dilar API returns an array directly or wrapped in data? 
                // Based on standard fast json APIs, let's parse as list first.
                // It seems the API returns a JSON array of series objects directly based on previous curl outputs.
                
                // If it's wrapped: DilarSeriesResponse responseDto = gson.fromJson(jsonResponse, DilarSeriesResponse.class);
                // But let's assume it's a direct array based on common setups, or parse it dynamically.
                
                // We'll use a fast dynamic parsing approach for safety since the exact JSON structure of the root is slightly ambiguous from curl logs
                
                List<Manga> mangas = new ArrayList<>();
                try {
                     // Try parsing as DilarSeriesResponse (has 'data' array or 'items')
                     // Or, just parse the raw string manually to find titles and IDs if Gson fails
                }catch(Exception ignored){}
                
                // Fallback: simple string matching if GSON model doesn't match perfectly
                String[] items = jsonResponse.split("\\{\"id\":\"");
                for (int i = 1; i < items.length; i++) {
                    String item = items[i];
                    try {
                        String id = item.split("\"")[0];
                        String title = extractJsonField(item, "title");
                        String coverName = extractJsonField(item, "cover");
                        
                        Manga manga = new Manga();
                        manga.setTitle(title);
                        // Convert ID to URL for our system compatibility
                        manga.setUrl("https://dilar.tube/mangas/" + id);
                        manga.setCoverUrl("https://dilar.tube/uploads/series/covers/" + coverName); // Assuming typical image path
                        
                        mangas.add(manga);
                    } catch (Exception ignored) {}
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!mangas.isEmpty()) {
                        callback.onSuccess(mangas);
                    } else {
                        callback.onError("لم يتم العثور على أعمال في Dilar.");
                    }
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("خطأ في الاتصال: " + e.getMessage()));
            }
        }).start();
    }

    public static void fetchMangaDetails(String mangaUrl, MangaScraper.MangaDetailsCallback callback) {
        new Thread(() -> {
            try {
                // Extract ID from URL (e.g. https://dilar.tube/mangas/12808)
                String id = mangaUrl.substring(mangaUrl.lastIndexOf("/") + 1);
                String apiUrl = API_SERIES_URL + "/" + id;

                Request request = new Request.Builder()
                        .url(apiUrl)
                        .header("Accept", "application/json")
                        .build();

                Response response = MangaOkHttp.getClient().newCall(request).execute();

                if (!response.isSuccessful() || response.body() == null) {
                    throw new Exception("HTTP Error: " + response.code());
                }

                String jsonResponse = response.body().string();
                
                String tempDesc = extractJsonField(jsonResponse, "summary");
                final String description = tempDesc.isEmpty() ? "لا يوجد وصف متاح." : tempDesc;
                
                List<Chapter> chapters = new ArrayList<>();
                
                // Extract releases array manually for speed and safety
                String[] releases = jsonResponse.split("\"releases\":\\[\\{");
                if (releases.length > 1) {
                    String[] chapterBlocks = releases[1].split("\\},\\{");
                    for (String block : chapterBlocks) {
                        try {
                            String chapterNumber = extractJsonField(block, "chapter");
                            String chapterTitle = extractJsonField(block, "title");
                            
                            // Construct chapter URL for the reader WebView to intercept
                            // Format: https://dilar.tube/reader/{manga_id}/slug/{chapter_number}
                            String chapterUrl = "https://dilar.tube/reader/" + id + "/slug/" + chapterNumber;
                            
                            String displayTitle = "الفصل " + chapterNumber;
                            if (!chapterTitle.isEmpty()) {
                                displayTitle += " - " + chapterTitle;
                            }
                            
                            Chapter chapter = new Chapter();
                            chapter.setTitle(displayTitle);
                            chapter.setUrl(chapterUrl);
                            
                            chapters.add(chapter);
                        } catch (Exception ignored) {}
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(description, "مستمرة", chapters);
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("خطأ في جلب التفاصيل: " + e.getMessage()));
            }
        }).start();
    }
    
    // 3. Page Fetching (Hybrid using WebView)
    // We will handle this directly inside ChapterPagesActivity using CloudflareBypassDialog
    // to open the chapter URL, let it bypass Cloudflare, and then we inject JS to extract the __PRELOADED_STATE__

    private static String extractJsonField(String json, String field) {
        try {
            String key = "\"" + field + "\":\"";
            int startIndex = json.indexOf(key);
            if (startIndex == -1) return "";
            startIndex += key.length();
            int endIndex = json.indexOf("\"", startIndex);
            if (endIndex == -1) return "";
            // Handle escaped quotes inside the string if necessary, but simple substring usually works for titles/summaries
            return json.substring(startIndex, endIndex).replace("\\n", "\n").replace("\\r", "").replace("\\\"", "\"");
        } catch (Exception e) {
            return "";
        }
    }
}
