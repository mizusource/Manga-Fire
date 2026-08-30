with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

like_method = """
    public void likeComment(String commentId, int currentLikes, final AuthCallback callback) {
        if (!isLoggedIn()) {
            postAuthCallback(callback, false, "يجب تسجيل الدخول للإعجاب");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("likes", currentLikes + 1);
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/manga_comments?id=eq." + commentId)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .patch(body)
                .build();
                        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                postAuthCallback(callback, false, e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم الإعجاب");
                } else {
                    String err = response.body() != null ? response.body().string() : "";
                    if(err.contains("PGRST303")) {
                        postAuthCallback(callback, false, "انتهت صلاحية الجلسة، سجل خروجك ثم ادخل مجدداً");
                    } else {
                        postAuthCallback(callback, false, "فشل الإعجاب: " + err);
                    }
                }
            }
        });
    }
"""

if 'public void likeComment' not in content:
    content = content.replace('// ==== RATINGS ====', like_method + '\n    // ==== RATINGS ====')
    with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
        f.write(content)
    print("Added likeComment to SupabaseManager")
else:
    print("likeComment already exists")
