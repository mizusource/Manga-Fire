import re

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

data_callback = """
    public interface DataCallback {
        void onSuccess(JSONArray data);
        void onError(String error);
    }
"""

new_methods = """
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

    private void postDataCallback(final DataCallback callback, final JSONArray data, final String error) {
        new Handler(Looper.getMainLooper()).post(() -> {
            if (callback != null) {
                if (data != null) callback.onSuccess(data);
                else callback.onError(error);
            }
        });
    }
"""

content = content.replace("public interface AuthCallback", data_callback + "\n" + new_methods + "\n    public interface AuthCallback")

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
    f.write(content)
