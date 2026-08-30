import os
import re

# 1. Update CommentAdapter.java to handle likes
adapter_path = 'app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java'
with open(adapter_path, 'r') as f:
    adapter = f.read()

old_like = 'holder.btnLike.setOnClickListener(v -> android.widget.Toast.makeText(context, "ميزة الإعجاب والردود قيد التطوير وسيتم تفعيلها قريباً", android.widget.Toast.LENGTH_SHORT).show());'

new_like = """holder.btnLike.setOnClickListener(v -> {
            if (context instanceof com.fire.mangareader.activity.MangaDetailActivity) {
                ((com.fire.mangareader.activity.MangaDetailActivity) context).likeComment(comment);
                comment.likes += 1;
                holder.tvLikeCount.setText(String.valueOf(comment.likes));
                holder.btnLike.setEnabled(false);
            }
        });"""
if old_like in adapter:
    adapter = adapter.replace(old_like, new_like)
    with open(adapter_path, 'w') as f:
        f.write(adapter)
    print("Patched CommentAdapter")

# 2. Update CommentsBottomSheetDialog.java to extract ID and handle likes
dialog_path = 'app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java'
with open(dialog_path, 'r') as f:
    dialog = f.read()

if 'comment.id = obj.optString("id");' not in dialog:
    dialog = dialog.replace(
        'comment.mangaUrl = obj.optString("manga_url");',
        'comment.id = obj.optString("id");\n                            comment.mangaUrl = obj.optString("manga_url");'
    )
    # also we need to extract user_id if possible
    dialog = dialog.replace(
        'comment.username = obj.optString("username");',
        'comment.username = obj.optString("username");\n                            comment.user_id = obj.optString("user_id");'
    )
    with open(dialog_path, 'w') as f:
        f.write(dialog)
    print("Patched CommentsBottomSheetDialog parsing")

# 3. Add likeComment to SupabaseManager
supa_path = 'app/src/main/java/com/fire/mangareader/network/SupabaseManager.java'
with open(supa_path, 'r') as f:
    supa = f.read()

like_func = """
    public void likeComment(String commentId, int newLikesCount, final AuthCallback callback) {
        if (!isLoggedIn()) {
            postAuthCallback(callback, false, "يجب تسجيل الدخول");
            return;
        }
        JSONObject json = new JSONObject();
        try {
            json.put("likes", newLikesCount);
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
                    postAuthCallback(callback, false, "فشل الإعجاب");
                }
            }
        });
    }
"""
if 'public void likeComment' not in supa:
    supa = supa.replace('// ==== RATINGS ====', like_func + '\n    // ==== RATINGS ====')

# Add sendNotification to SupabaseManager
notif_func = """
    public void sendNotification(String targetUserId, String title, String message, String mangaUrl, final AuthCallback callback) {
        if (!isLoggedIn()) return;
        JSONObject json = new JSONObject();
        try {
            json.put("user_id", targetUserId);
            json.put("sender_name", title);
            json.put("message", message);
            json.put("manga_url", mangaUrl);
            json.put("type", "notification");
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
            public void onFailure(Call call, IOException e) {
                if(callback != null) postAuthCallback(callback, false, e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(callback != null) postAuthCallback(callback, response.isSuccessful(), "");
            }
        });
    }
    
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
                        postDataCallback(callback, null, "Error parsing");
                    }
                } else {
                    postDataCallback(callback, null, "Failed");
                }
            }
        });
    }
"""
if 'public void sendNotification' not in supa:
    supa = supa.replace('// ==== RATINGS ====', notif_func + '\n    // ==== RATINGS ====')

with open(supa_path, 'w') as f:
    f.write(supa)
print("Patched SupabaseManager")
