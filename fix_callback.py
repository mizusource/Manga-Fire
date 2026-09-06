import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

bad_callback = """
                    com.fire.mangareader.data.network.SupabaseManager.getInstance(MangaDetailActivity.this).addToLibrary(mangaUrl, mangaTitle, mangaCover, finalStatus, new com.fire.mangareader.data.network.SupabaseManager.DataCallback() {
                        @Override
                        public void onSuccess(org.json.JSONArray data) { }
                        @Override
                        public void onError(String error) { }
                    });
"""

good_callback = """
                    com.fire.mangareader.data.network.SupabaseManager.getInstance(MangaDetailActivity.this).addToLibrary(mangaUrl, mangaTitle, mangaCover, finalStatus, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {
                        @Override
                        public void onResult(boolean success, String message) { }
                    });
"""
content = content.replace(bad_callback.strip(), good_callback.strip())

# Clean up duplicate btnNotification if any
content = re.sub(r'btnNotification = findViewById\(R\.id\.btnNotification\);\n        btnNotification = findViewById\(R\.id\.btnNotification\);', 'btnNotification = findViewById(R.id.btnNotification);', content)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

