import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/LibraryActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import com.fire.mangareader.data.network.SupabaseManager;', 'import com.fire.mangareader.data.network.SupabaseManager;\nimport com.fire.mangareader.data.database.AppDatabase;\nimport com.fire.mangareader.data.database.LibraryItem;')

old_load = """    private void loadLibraryFromSupabase() {
        if (!SupabaseManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "يرجى تسجيل الدخول لعرض المكتبة", Toast.LENGTH_SHORT).show();
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvLibrary.setVisibility(View.GONE);
            return;
        }
        SupabaseManager.getInstance(this).getUserLibrary(new SupabaseManager.DataCallback() {
            @Override
            public void onSuccess(JSONArray data) {
                List<Manga> mappedList = new ArrayList<>();
                try {
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        Manga manga = new Manga();
                        manga.setTitle(obj.optString("manga_title", "مجهول"));
                        manga.setUrl(obj.getString("manga_url"));
                        manga.setCoverUrl(obj.optString("cover_url", ""));
                        manga.setRating("❤️"); // Can change based on status
                        
                        String status = obj.optString("status", "reading");
                        String statusAr = status;
                        if (status.equals("reading")) statusAr = "أقرأها حالياً";
                        else if (status.equals("plan_to_read")) statusAr = "سأقرأها";
                        else if (status.equals("completed")) statusAr = "مكتملة";
                        else if (status.equals("favorite")) statusAr = "مفضلة";
                        
                        manga.setLatestChapter(statusAr);
                        
                        // We can store original status in some unused field or just rely on latestChapter for filtering
                        // Let's store original status in 'rating' temporarily or extend Manga model.
                        manga.setRating("❤️"); 
                        
                        mappedList.add(manga);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                allLibraryItems.clear();
                allLibraryItems.addAll(mappedList);
                filterList(tabLayout.getSelectedTabPosition());
            }
            @Override
            public void onError(String error) {
                Toast.makeText(LibraryActivity.this, "فشل جلب المكتبة", Toast.LENGTH_SHORT).show();
            }
        });
    }"""

new_load = """    private void loadLibraryFromSupabase() {
        AppDatabase.getInstance(this).mangaDao().getAllLibraryItems().observe(this, items -> {
            List<Manga> mappedList = new ArrayList<>();
            if (items != null) {
                for (LibraryItem item : items) {
                    Manga manga = new Manga();
                    manga.setTitle(item.getTitle() != null ? item.getTitle() : "مجهول");
                    manga.setUrl(item.getMangaId());
                    manga.setCoverUrl(item.getCoverUrl());
                    
                    String status = item.getStatus();
                    if (status == null) status = "";
                    String statusAr = status;
                    if (status.equals("reading")) statusAr = "أقرأها حالياً";
                    else if (status.equals("plan_to_read")) statusAr = "سأقرأها";
                    else if (status.equals("completed")) statusAr = "مكتملة";
                    
                    if (item.isFavorite() && (status == null || status.isEmpty())) statusAr = "مفضلة";
                    
                    manga.setLatestChapter(statusAr);
                    manga.setRating("❤️"); 
                    
                    mappedList.add(manga);
                }
            }
            allLibraryItems.clear();
            allLibraryItems.addAll(mappedList);
            filterList(tabLayout.getSelectedTabPosition());
        });
    }"""

content = content.replace(old_load, new_load)

# Add isFavorite mapping support in filterList
old_filter = """        for (Manga manga : allLibraryItems) {
            if (tabPosition == 0 || (manga.getLatestChapter() != null && manga.getLatestChapter().equals(targetStatusAr))) {
                displayList.add(manga);
            }
        }"""

new_filter = """        for (Manga manga : allLibraryItems) {
            if (tabPosition == 0 || (manga.getLatestChapter() != null && manga.getLatestChapter().equals(targetStatusAr))) {
                displayList.add(manga);
            }
        }"""
        
content = content.replace(old_filter, new_filter)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched LibraryActivity.java")
