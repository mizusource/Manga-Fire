import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

new_check = """
    private void checkLibraryStatus() {
        // 1. Check local DB first for instant UI response
        new Thread(() -> {
            com.fire.mangareader.data.database.LibraryItem item = AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().getItemById(mangaUrl);
            if (item != null) {
                String status = item.getStatus();
                if (status != null && !status.isEmpty()) {
                    currentLibraryStatus = status;
                } else if (item.isFavorite()) {
                    currentLibraryStatus = "favorite";
                }
                runOnUiThread(this::updateFavoriteIcon);
            }
            
            // 2. Fetch from Supabase silently to sync if logged in
            if (com.fire.mangareader.data.network.SupabaseManager.getInstance(MangaDetailActivity.this).isLoggedIn()) {
                com.fire.mangareader.data.network.SupabaseManager.getInstance(MangaDetailActivity.this).checkLibraryStatus(mangaUrl, new com.fire.mangareader.data.network.SupabaseManager.DataCallback() {
                    @Override
                    public void onSuccess(org.json.JSONArray data) {
                        try {
                            if (data.length() > 0) {
                                currentLibraryStatus = data.getJSONObject(0).getString("status");
                                runOnUiThread(() -> updateFavoriteIcon());
                                
                                // Update local db in background
                                new Thread(() -> {
                                    com.fire.mangareader.data.database.LibraryItem syncItem = AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().getItemById(mangaUrl);
                                    if (syncItem == null) {
                                        syncItem = new com.fire.mangareader.data.database.LibraryItem();
                                        syncItem.setMangaId(mangaUrl);
                                        syncItem.setTitle(mangaTitle);
                                        syncItem.setCoverUrl(mangaCover);
                                        syncItem.setAddedTime(System.currentTimeMillis());
                                    }
                                    if (currentLibraryStatus.equals("favorite")) {
                                        syncItem.setFavorite(true);
                                    } else {
                                        syncItem.setStatus(currentLibraryStatus);
                                    }
                                    AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().insert(syncItem);
                                }).start();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String error) {
                    }
                });
            }
        }).start();
    }
"""

content = re.sub(r'private void checkLibraryStatus\(\) \{.*?(?=private void loadAniListMetadata)', new_check.strip() + "\n\n    ", content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

