with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

update_token_method = """
    public void updateFcmToken(String token) {
        if (!isLoggedIn()) return;
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", currentUserId);
            json.put("token", token);
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/user_tokens")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(body)
                .build();
                
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}
            @Override
            public void onResponse(Call call, Response response) throws IOException {}
        });
    }
"""

if 'public void updateFcmToken' not in content:
    content = content.replace(
        'public interface AuthCallback {',
        update_token_method + '\n    public interface AuthCallback {'
    )
    with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
        f.write(content)
    print("Added updateFcmToken")
