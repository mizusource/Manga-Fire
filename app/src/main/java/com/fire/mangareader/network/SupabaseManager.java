package com.fire.mangareader.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseManager {
    private static final String SUPABASE_URL = "https://hympamxaenscfjetijay.supabase.co";
    private static final String SUPABASE_KEY = "sb_publishable_jemx2yodZ6PN8sLPgAn8Iw_4frKfERp";
    
    private static SupabaseManager instance;
    private OkHttpClient client;
    private SharedPreferences prefs;
    private String accessToken;
    private String currentUserId;
    
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    
    private SupabaseManager(Context context) {
        client = new OkHttpClient();
        prefs = context.getApplicationContext().getSharedPreferences("supabase_prefs", Context.MODE_PRIVATE);
        accessToken = prefs.getString("access_token", null);
        currentUserId = prefs.getString("user_id", null);
    }
    
    public static synchronized SupabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new SupabaseManager(context);
        }
        return instance;
    }
    
    public boolean isLoggedIn() {
        return accessToken != null && currentUserId != null;
    }
    
    public String getCurrentUserId() {
        return currentUserId;
    }
    
    // Auth: Sign Up
    public void signUp(String email, String password, final AuthCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/auth/v1/signup")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postAuthCallback(callback, false, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم التسجيل بنجاح!");
                } else {
                    postAuthCallback(callback, false, "فشل التسجيل: " + response.message());
                }
            }
        });
    }

    // Auth: Sign In
    
    // Auth: Reset Password
    public void resetPassword(String email, final AuthCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/auth/v1/recover")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postAuthCallback(callback, false, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم إرسال رابط إعادة التعيين إلى بريدك الإلكتروني");
                } else {
                    postAuthCallback(callback, false, "فشل إرسال رابط إعادة التعيين، تحقق من البريد");
                }
            }
        });
    }

    public void signIn(String email, String password, final AuthCallback callback) {
        JSONObject json = new JSONObject();
        try {
            json.put("email", email);
            json.put("password", password);
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/auth/v1/token?grant_type=password")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postAuthCallback(callback, false, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String respStr = response.body().string();
                        JSONObject resJson = new JSONObject(respStr);
                        accessToken = resJson.getString("access_token");
                        currentUserId = resJson.getJSONObject("user").getString("id");
                        
                        prefs.edit()
                            .putString("access_token", accessToken)
                            .putString("user_id", currentUserId)
                            .apply();
                            
                        postAuthCallback(callback, true, "تم تسجيل الدخول بنجاح");
                    } catch (Exception e) {
                        postAuthCallback(callback, false, "خطأ في معالجة البيانات");
                    }
                } else {
                    try {
                        String errorBody = response.body().string();
                        org.json.JSONObject errorObj = new org.json.JSONObject(errorBody);
                        String msg = errorObj.optString("error_description", errorObj.optString("msg", "البريد أو كلمة المرور غير صحيحة"));
                        
                        if (msg.contains("Email not confirmed") || msg.contains("verify")) {
                            msg = "لتفعيل حسابك قم بالدخول الى بريدك الالكتروني واضغط تأكيد";
                        } else if (msg.contains("Invalid login credentials")) {
                            msg = "البريد أو كلمة المرور غير صحيحة.";
                        }
                        
                        postAuthCallback(callback, false, msg);
                    } catch (Exception ex) {
                        postAuthCallback(callback, false, "البريد أو كلمة المرور غير صحيحة");
                    }
                }
            }
        });
    }
    
    public void signOut() {
        accessToken = null;
        currentUserId = null;
        prefs.edit().clear().apply();
    }
    
    // Library Methods
    public void addToLibrary(String mangaUrl, String mangaTitle, String coverUrl, String status, final AuthCallback callback) {
        if (!isLoggedIn()) {
            postAuthCallback(callback, false, "يجب تسجيل الدخول أولاً");
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("user_id", currentUserId);
            json.put("manga_url", mangaUrl);
            json.put("manga_title", mangaTitle);
            json.put("cover_url", coverUrl);
            json.put("status", status); // 'reading', 'plan_to_read', 'completed', 'dropped', 'favorite'
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        // Using upsert (on_conflict) to update status if manga already in library
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/user_library?on_conflict=user_id,manga_url")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(body)
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postAuthCallback(callback, false, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم الحفظ في القائمة");
                } else {
                    postAuthCallback(callback, false, "فشل الحفظ: " + response.code());
                }
            }
        });
    }

    public void markChapterRead(String mangaUrl, String chapterUrl, String chapterTitle, final AuthCallback callback) {
        if (!isLoggedIn()) {
            postAuthCallback(callback, false, "يجب تسجيل الدخول");
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("user_id", currentUserId);
            json.put("manga_url", mangaUrl);
            json.put("chapter_url", chapterUrl);
            json.put("chapter_title", chapterTitle);
        } catch (Exception e) {}

        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/read_history")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postAuthCallback(callback, false, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم حفظ السجل");
                } else {
                    postAuthCallback(callback, false, "فشل حفظ السجل");
                }
            }
        });
    }
    
    private void postAuthCallback(final AuthCallback callback, final boolean success, final String message) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (callback != null) {
                if (success) callback.onSuccess(message);
                else callback.onError(message);
            }
        });
    }

    
    public interface DataCallback {
        void onSuccess(JSONArray data);
        void onError(String error);
    }


    public void getUserLibrary(final DataCallback callback) {
        if (!isLoggedIn()) {
            postDataCallback(callback, null, "يجب تسجيل الدخول");
            return;
        }

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/user_library?user_id=eq." + currentUserId + "&select=*")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postDataCallback(callback, null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        postDataCallback(callback, array, null);
                    } catch (Exception e) {
                        postDataCallback(callback, null, "Error parsing data");
                    }
                } else {
                    postDataCallback(callback, null, "Failed to fetch library");
                }
            }
        });
    }

    public void checkLibraryStatus(String mangaUrl, final DataCallback callback) {
        if (!isLoggedIn()) {
            postDataCallback(callback, null, "يجب تسجيل الدخول");
            return;
        }

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/user_library?user_id=eq." + currentUserId + "&manga_url=eq." + mangaUrl + "&select=*")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postDataCallback(callback, null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        postDataCallback(callback, array, null);
                    } catch (Exception e) {
                        postDataCallback(callback, null, "Error parsing data");
                    }
                } else {
                    postDataCallback(callback, null, "Failed to check status");
                }
            }
        });
    }

    public void removeFromLibrary(String mangaUrl, final AuthCallback callback) {
        if (!isLoggedIn()) {
            postAuthCallback(callback, false, "يجب تسجيل الدخول");
            return;
        }

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/user_library?user_id=eq." + currentUserId + "&manga_url=eq." + mangaUrl)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .delete()
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postAuthCallback(callback, false, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم الإزالة من القائمة");
                } else {
                    postAuthCallback(callback, false, "فشل الإزالة");
                }
            }
        });
    }

    
    // ==== COMMENTS ====
    public void getComments(String mangaUrl, final DataCallback callback) {
        String encodedUrl = android.net.Uri.encode(mangaUrl);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/manga_comments?manga_url=eq." + encodedUrl + "&order=created_at.desc&select=*")
                .addHeader("apikey", SUPABASE_KEY)
                .build();
                        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postDataCallback(callback, null, e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        postDataCallback(callback, array, null);
                    } catch (Exception e) {
                        postDataCallback(callback, null, "Error parsing data");
                    }
                } else {
                    postDataCallback(callback, null, "Failed to fetch comments");
                }
            }
        });
    }

    public void addComment(String mangaUrl, String text, boolean isSpoiler, String userName, final AuthCallback callback) {
        if (!isLoggedIn()) {
            postAuthCallback(callback, false, "يجب تسجيل الدخول أولاً");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", currentUserId);
            json.put("manga_url", mangaUrl);
            json.put("username", userName != null ? userName : "User");
            json.put("text", text);
            json.put("is_spoiler", isSpoiler);
            json.put("likes", 0);
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/manga_comments")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();
                        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postAuthCallback(callback, false, e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم إضافة التعليق");
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "";
                    postAuthCallback(callback, false, "فشل إضافة التعليق: " + errorBody);
                }
            }
        });
    }

    // ==== RATINGS ====
    public void submitRating(String mangaUrl, float overall, float story, float characters, float art, final AuthCallback callback) {
        if (!isLoggedIn()) {
            postAuthCallback(callback, false, "يجب تسجيل الدخول أولاً");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("manga_url", mangaUrl);
            json.put("user_id", currentUserId);
            json.put("overall", overall);
            json.put("story", story);
            json.put("characters", characters);
            json.put("art", art);
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/manga_ratings?on_conflict=manga_url,user_id")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(body)
                .build();
                        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postAuthCallback(callback, false, e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم حفظ التقييم");
                } else {
                    postAuthCallback(callback, false, "فشل حفظ التقييم");
                }
            }
        });
    }

    public void getMangaStats(String mangaUrl, final DataCallback callback) {
        String encodedUrl = android.net.Uri.encode(mangaUrl);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/manga_ratings?manga_url=eq." + encodedUrl + "&select=overall,story,characters,art")
                .addHeader("apikey", SUPABASE_KEY)
                .build();
                        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postDataCallback(callback, null, e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().string());
                        postDataCallback(callback, array, null);
                    } catch (Exception e) {
                        postDataCallback(callback, null, "Error parsing data");
                    }
                } else {
                    postDataCallback(callback, null, "Failed to fetch stats");
                }
            }
        });
    }

    private void postDataCallback(final DataCallback callback, final JSONArray data, final String error) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (callback != null) {
                if (data != null) callback.onSuccess(data);
                else callback.onError(error);
            }
        });
    }

    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
