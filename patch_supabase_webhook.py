with open("app/src/main/java/com/fire/mangareader/data/network/SupabaseManager.java", "r") as f:
    content = f.read()

trigger_code = """
    private void triggerMakeWebhook(String actionType, JSONObject commentData) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("action_type", actionType);
            payload.put("data", commentData);
            
            RequestBody body = RequestBody.create(payload.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://hook.eu1.make.com/ud10lj71nvofrtucj1jq5ls6te8jqtdc")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, java.io.IOException e) {}
                @Override public void onResponse(Call call, Response response) {}
            });
        } catch (Exception e) {}
    }
"""

if "triggerMakeWebhook" not in content:
    # insert before currentUserId
    content = content.replace("public String getCurrentUserId()", trigger_code + "\n    public String getCurrentUserId()")

# Patch addComment
if "triggerMakeWebhook(\"new_comment\"" not in content:
    add_comment_success = """                if (response.isSuccessful()) {
                    triggerMakeWebhook("new_comment", json);
                    postAuthCallback(callback, true, "تم إضافة التعليق");"""
    content = content.replace("""                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم إضافة التعليق");""", add_comment_success)

# Patch addReply
if "triggerMakeWebhook(\"new_reply\"" not in content:
    add_reply_success = """                if (response.isSuccessful()) {
                    triggerMakeWebhook("new_reply", json);
                    postAuthCallback(callback, true, "تم إضافة الرد");"""
    content = content.replace("""                if (response.isSuccessful()) {
                    postAuthCallback(callback, true, "تم إضافة الرد");""", add_reply_success)

with open("app/src/main/java/com/fire/mangareader/data/network/SupabaseManager.java", "w") as f:
    f.write(content)
