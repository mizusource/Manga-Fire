package com.fire.mangareader.utils;

import android.util.Log;
import com.fire.mangareader.model.AniListMetadata;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AniListManager {
    private static final String TAG = "AniListManager";
    private static final String ANILIST_GRAPHQL_URL = "https://graphql.anilist.co";

    public interface MetadataCallback {
        void onSuccess(AniListMetadata metadata);
        void onError(String error);
    }

    public interface AniListCallback {
        void onSuccess(AniListMetadata metadata);
        void onError(String error);
    }

    public static void fetchMetadata(String mangaTitle, AniListCallback callback) {
        fetchMetadataInternal(mangaTitle, new MetadataCallback() {
            @Override
            public void onSuccess(AniListMetadata metadata) {
                if (callback != null) callback.onSuccess(metadata);
            }

            @Override
            public void onError(String error) {
                if (callback != null) callback.onError(error);
            }
        });
    }

    public static void fetchMetadata(String mangaTitle, MetadataCallback callback) {
        fetchMetadataInternal(mangaTitle, callback);
    }

    private static void fetchMetadataInternal(String mangaTitle, MetadataCallback callback) {
        new Thread(() -> {
            try {
                String cleanTitle = cleanSearchTitle(mangaTitle);
                String query = "query ($search: String) { " +
                        "Media (search: $search, type: MANGA) { " +
                        "id " +
                        "title { romaji english native } " +
                        "format " +
                        "countryOfOrigin " +
                        "startDate { year month day } " +
                        "endDate { year month day } " +
                        "status " +
                        "averageScore " +
                        "description(asHtml: false) " +
                        "bannerImage " +
                        "coverImage { extraLarge large } " +
                        "staff { edges { role node { name { full } } } } " +
                        "} }";

                JSONObject variables = new JSONObject();
                variables.put("search", cleanTitle);

                JSONObject payload = new JSONObject();
                payload.put("query", query);
                payload.put("variables", variables);

                RequestBody body = RequestBody.create(
                        payload.toString(),
                        MediaType.parse("application/json; charset=utf-8")
                );

                Request request = new Request.Builder()
                        .url(ANILIST_GRAPHQL_URL)
                        .post(body)
                        .header("Accept", "application/json")
                        .build();

                Response response = MangaOkHttp.getClient().newCall(request).execute();
                if (response.isSuccessful() && response.body() != null) {
                    String json = response.body().string();
                    response.close();
                    JSONObject root = new JSONObject(json);
                    JSONObject data = root.optJSONObject("data");
                    if (data != null && data.has("Media")) {
                        JSONObject media = data.getJSONObject("Media");
                        AniListMetadata meta = new AniListMetadata();
                        meta.anilistId = media.optInt("id", 0);
                        meta.sourceFormat = media.optString("format", "MANGA");
                        meta.format = MangaExtensions.getMangaFormat(meta.sourceFormat);
                        meta.originCountry = media.optString("countryOfOrigin", "JP");
                        meta.countryOfOrigin = MangaExtensions.getMangaType(meta.originCountry);
                        
                        int avgScore = media.optInt("averageScore", 0);
                        meta.averageScore = avgScore;
                        meta.popularity = media.optInt("popularity", 1250);
                        meta.scoreText = avgScore > 0 ? (avgScore / 10.0) + " / 10" : "غير مقيّم";
                        
                        meta.bannerUrl = media.optString("bannerImage", "");
                        JSONObject coverImg = media.optJSONObject("coverImage");
                        if (coverImg != null) {
                            meta.coverUrl = coverImg.optString("extraLarge", coverImg.optString("large", ""));
                        }

                        meta.statusArabic = MangaExtensions.getMangaStatus(media.optString("status", ""));
                        meta.startDateText = formatDate(media.optJSONObject("startDate"));
                        meta.endDateText = formatDate(media.optJSONObject("endDate"));
                        meta.synopsisArabic = media.optString("description", "");

                        // Staff
                        JSONObject staff = media.optJSONObject("staff");
                        if (staff != null) {
                            JSONArray edges = staff.optJSONArray("edges");
                            if (edges != null) {
                                for (int i = 0; i < edges.length(); i++) {
                                    JSONObject edge = edges.getJSONObject(i);
                                    String role = edge.optString("role", "").toLowerCase();
                                    JSONObject node = edge.optJSONObject("node");
                                    if (node != null) {
                                        JSONObject name = node.optJSONObject("name");
                                        String personName = name != null ? name.optString("full", "") : "";
                                        if (role.contains("story") || role.contains("author") || role.contains("original")) {
                                            if (meta.author.isEmpty()) meta.author = personName;
                                        }
                                        if (role.contains("art") || role.contains("illustrator")) {
                                            if (meta.artist.isEmpty()) meta.artist = personName;
                                        }
                                    }
                                }
                            }
                        }

                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                            callback.onSuccess(meta);
                        });
                        return;
                    }
                }
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError("لم يتم العثور على تفاصيل في AniList");
                });
            } catch (Exception e) {
                Log.e(TAG, "Error fetching AniList metadata", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    callback.onError(e.getMessage());
                });
            }
        }).start();
    }

    private static String mapStatusToArabic(String status) {
        if ("FINISHED".equalsIgnoreCase(status)) return "مكتملة";
        if ("RELEASING".equalsIgnoreCase(status)) return "مستمرة";
        if ("NOT_YET_RELEASED".equalsIgnoreCase(status)) return "قريباً";
        if ("CANCELLED".equalsIgnoreCase(status)) return "ملغاة";
        if ("HIATUS".equalsIgnoreCase(status)) return "متوقفة مؤقتاً";
        return status;
    }

    private static String formatDate(JSONObject dateObj) {
        if (dateObj == null) return "غير محدد";
        int y = dateObj.optInt("year", 0);
        int m = dateObj.optInt("month", 0);
        int d = dateObj.optInt("day", 0);
        if (y <= 0) return "غير محدد";
        String[] months = {"يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر"};
        String monthName = (m >= 1 && m <= 12) ? months[m - 1] : "";
        if (d > 0 && !monthName.isEmpty()) return d + " " + monthName + " " + y;
        if (!monthName.isEmpty()) return monthName + " " + y;
        return String.valueOf(y);
    }

    private static String cleanSearchTitle(String title) {
        if (title == null) return "";
        return title.replaceAll("\\(.*?\\)|\\[.*?\\]", "")
                .replaceAll("مترجم|مانجا|مانهوا|فصل|تلوين|كامل", "")
                .trim();
    }
}
