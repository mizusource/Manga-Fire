import re

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

reset_method = """
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
"""

content = content.replace('public void signIn(String email, String password, final AuthCallback callback) {', reset_method + '\n    public void signIn(String email, String password, final AuthCallback callback) {')

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
    f.write(content)
