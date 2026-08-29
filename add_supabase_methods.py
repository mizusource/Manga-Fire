import os

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

new_methods = """
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
                    postAuthCallback(callback, false, "فشل إضافة التعليق");
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
"""

content = content.replace("private void postDataCallback", new_methods + "\n    private void postDataCallback")

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
    f.write(content)

