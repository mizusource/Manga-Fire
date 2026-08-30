with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

send_notif = """
    public void sendNotification(String toUserId, String senderName, String message, String mangaUrl, String type) {
        if (!isLoggedIn()) return;
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", toUserId);
            json.put("sender_name", senderName);
            json.put("message", message);
            json.put("manga_url", mangaUrl);
            json.put("type", type);
            json.put("is_read", false);
        } catch (Exception e) {}
        
        RequestBody body = RequestBody.create(json.toString(), JSON);
        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/notifications")
                .addHeader("apikey", SUPABASE_KEY)
                .addHeader("Authorization", "Bearer " + accessToken)
                .addHeader("Content-Type", "application/json")
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

if 'public void sendNotification' not in content:
    content = content.replace(
        'public interface AuthCallback {',
        send_notif + '\n    public interface AuthCallback {'
    )
    with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
        f.write(content)
    print("Added sendNotification")
