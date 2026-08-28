with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'r') as f:
    content = f.read()

import re
old_logic = '''                List<LibraryItem> items = AppDatabase.getInstance(this).mangaDao().getAllItems();
                
                List<Manga> mappedList = new ArrayList<>();
                String filterStatus = getIntent().getStringExtra("FILTER_STATUS");
                for (LibraryItem item : items) {
                    boolean matchesFilter = false;
                    if (filterStatus != null && !filterStatus.isEmpty()) {
                        matchesFilter = filterStatus.equals(item.getStatus());
                    } else {
                        matchesFilter = item.isFavorite();
                    }
                    
                    if (matchesFilter) {
                        Manga manga = new Manga();
                        manga.setTitle(item.getTitle());
                        manga.setUrl(item.getMangaId());
                        manga.setCoverUrl(item.getCoverUrl());
                        manga.setRating("❤️"); // وضع قلب كتقييم لتمييزها
                        manga.setLatestChapter(item.getStatus() != null ? item.getStatus() : "مفضلة"); 
                        mappedList.add(manga);
                    }
                }'''

new_logic = '''                String filterStatus = getIntent().getStringExtra("FILTER_STATUS");
                List<LibraryItem> items;
                if (filterStatus != null && !filterStatus.isEmpty()) {
                    items = AppDatabase.getInstance(this).mangaDao().getAllItems();
                } else {
                    items = AppDatabase.getInstance(this).mangaDao().getAllFavorites();
                }
                
                List<Manga> mappedList = new ArrayList<>();
                for (LibraryItem item : items) {
                    boolean matchesFilter = false;
                    if (filterStatus != null && !filterStatus.isEmpty()) {
                        matchesFilter = filterStatus.equals(item.getStatus());
                    } else {
                        matchesFilter = true; // since we already fetched only favorites
                    }
                    
                    if (matchesFilter) {
                        Manga manga = new Manga();
                        manga.setTitle(item.getTitle() != null ? item.getTitle() : "مجهول");
                        manga.setUrl(item.getMangaId());
                        manga.setCoverUrl(item.getCoverUrl());
                        manga.setRating("❤️"); // وضع قلب كتقييم لتمييزها
                        manga.setLatestChapter(item.getStatus() != null ? item.getStatus() : "مفضلة"); 
                        mappedList.add(manga);
                    }
                }'''

content = content.replace(old_logic, new_logic)

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'w') as f:
    f.write(content)
