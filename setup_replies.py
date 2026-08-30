import os

# 1. Update SupabaseManager to support replies
with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

get_replies = """
    public void getReplies(String parentId, final DataCallback callback) {
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/manga_comments?parent_id=eq." + parentId + "&order=created_at.asc&select=*")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + SUPABASE_KEY)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postDataCallback(callback, null, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        postDataCallback(callback, jsonArray, null);
                    } catch (Exception e) {
                        postDataCallback(callback, null, "خطأ في تحليل البيانات");
                    }
                } else {
                    postDataCallback(callback, null, "فشل في تحميل الردود");
                }
            }
        });
    }

    public void addReply(String mangaUrl, String parentId, String text, boolean isSpoiler, String userName, final AuthCallback callback) {
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
            json.put("parent_id", parentId);
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
                    postAuthCallback(callback, true, "تمت إضافة الرد بنجاح");
                } else {
                    postAuthCallback(callback, false, "فشل في إضافة الرد");
                }
            }
        });
    }
"""

if 'public void getReplies' not in content:
    content = content.replace('public void deleteComment', get_replies + '\n    public void deleteComment')
    # Make sure we only get root comments (parent_id is null) in getComments
    content = content.replace('/rest/v1/manga_comments?manga_url=eq." + encodedUrl + "&order=created_at.desc&select=*"',
                            '/rest/v1/manga_comments?manga_url=eq." + encodedUrl + "&parent_id=is.null&order=created_at.desc&select=*"')
    with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
        f.write(content)

