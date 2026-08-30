with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

delete_method = """
    public void deleteComment(String commentId, final AuthCallback callback) {
        if (!isLoggedIn()) {
            postAuthCallback(callback, false, "يجب تسجيل الدخول");
            return;
        }

        Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/manga_comments?id=eq." + commentId)
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
                    postAuthCallback(callback, true, "تم الحذف بنجاح");
                } else {
                    postAuthCallback(callback, false, "فشل الحذف");
                }
            }
        });
    }
"""

if 'public void deleteComment' not in content:
    content = content.replace('public interface AuthCallback', delete_method + '\n    public interface AuthCallback')
    with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
        f.write(content)
