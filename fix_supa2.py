with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

notifs_methods = """
    // ==== NOTIFICATIONS ====
    public void getNotifications(final DataCallback callback) {
        if (!isLoggedIn()) {
            postDataCallback(callback, null, "يجب تسجيل الدخول");
            return;
        }
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/notifications?user_id=eq." + currentUserId + "&order=created_at.desc")
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
                        postDataCallback(callback, null, "Error parsing notifications");
                    }
                } else {
                    postDataCallback(callback, null, "Failed to fetch notifications");
                }
            }
        });
    }

    public void markNotificationAsRead(String notificationId, final AuthCallback callback) {
        if (!isLoggedIn()) {
            if (callback != null) postAuthCallback(callback, false, "يجب تسجيل الدخول");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("is_read", true);
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/notifications?id=eq." + notificationId)
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .patch(body)
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) postAuthCallback(callback, false, e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    if (response.isSuccessful()) {
                        postAuthCallback(callback, true, "Read");
                    } else {
                        postAuthCallback(callback, false, "Failed");
                    }
                }
            }
        });
    }
"""

if 'public void getNotifications' not in content:
    content = content.replace(
        'public interface AuthCallback {',
        notifs_methods + '\n    public interface AuthCallback {'
    )
    with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
        f.write(content)
    print("Added properly")
else:
    print("Already exists")
