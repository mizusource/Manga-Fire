import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CustomListManagerActivity.java", "r") as f:
    content = f.read()

content = content.replace("Toast.newInstance", "Toast.makeText")
content = content.replace("AppDatabase.getInstance(this).customListDao()", "com.fire.mangareader.data.local.AppDatabase.Companion.getDatabase(this).customListDao()")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CustomListManagerActivity.java", "w") as f:
    f.write(content)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

# Fix checkLibraryStatus DataCallback vs AuthCallback
content = content.replace("new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {\n            @Override\n            public void onResult", "new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {\n            @Override\n            public void onSuccess")
# checkLibraryStatus requires DataCallback!
content = content.replace("checkLibraryStatus(mangaUrl, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {\n            @Override\n            public void onSuccess(org.json.JSONArray data)", "checkLibraryStatus(mangaUrl, new com.fire.mangareader.data.network.SupabaseManager.DataCallback() {\n            @Override\n            public void onSuccess(org.json.JSONArray data)")
content = content.replace("checkLibraryStatus(mangaUrl, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback()", "checkLibraryStatus(mangaUrl, new com.fire.mangareader.data.network.SupabaseManager.DataCallback()")

# Fix addToLibrary AuthCallback
# addToLibrary takes AuthCallback, which has onSuccess(String message) and onError(String error)
content = content.replace("addToLibrary(mangaUrl, mangaTitle, mangaCover, finalStatus, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {\n                        @Override\n                        public void onSuccess(org.json.JSONArray data)", "addToLibrary(mangaUrl, mangaTitle, mangaCover, finalStatus, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {\n                        @Override\n                        public void onSuccess(String message)")
content = content.replace("addToLibrary(mangaUrl, mangaTitle, mangaCover, finalStatus, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {\n                        @Override\n                        public void onResult(boolean success, String message) { }", "addToLibrary(mangaUrl, mangaTitle, mangaCover, finalStatus, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {\n                        @Override\n                        public void onSuccess(String message) { }\n                        @Override\n                        public void onError(String error) { }\n")

# Missing openCustomListManager
open_method = """
    private void openCustomListManager() {
        android.content.Intent intent = new android.content.Intent(this, CustomListManagerActivity.class);
        intent.putExtra("mangaUrl", mangaUrl);
        intent.putExtra("mangaTitle", mangaTitle);
        intent.putExtra("mangaCover", mangaCover);
        startActivity(intent);
    }
    
    private void updateFavoriteIcon() {
        if (btnFavorite == null) return;
        if (currentLibraryStatus.equals("favorite") || currentLibraryStatus.equals("reading") || currentLibraryStatus.equals("completed") || currentLibraryStatus.equals("plan_to_read")) {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
            btnFavorite.setColorFilter(android.graphics.Color.parseColor("#E91E63"));
        } else {
            btnFavorite.setImageResource(android.R.drawable.btn_star_big_off);
            btnFavorite.setColorFilter(android.graphics.Color.WHITE);
        }
    }
"""
if "private void openCustomListManager" not in content:
    content = content.replace("public class MangaDetailActivity extends AppCompatActivity {", "public class MangaDetailActivity extends AppCompatActivity {\n" + open_method)

# Remove the broken toggleNotificationSubscription call if method is missing
if "private void toggleNotificationSubscription" not in content:
    content = content.replace("btnNotification.setOnClickListener(v -> toggleNotificationSubscription());", "")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

