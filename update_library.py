import re

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'r') as f:
    content = f.read()

# Inside loadFavoritesFromDatabase
old_block = """                for (LibraryItem item : items) {
                    if (item.isFavorite()) {
                        Manga manga = new Manga();
                        manga.setTitle(item.getTitle());
                        manga.setUrl(item.getMangaId());
                        manga.setCoverUrl(item.getCoverUrl());
                        manga.setRating("❤️"); // وضع قلب كتقييم لتمييزها
                        manga.setLatestChapter("مفضلة"); 
                        mappedList.add(manga);
                    }
                }"""

new_block = """                String filterStatus = getIntent().getStringExtra("FILTER_STATUS");
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
                }"""

content = content.replace(old_block, new_block)

# Also update the title of the Toolbar
old_title_block = """        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }"""
        
new_title_block = """        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String filterStatus = getIntent().getStringExtra("FILTER_STATUS");
            if (filterStatus != null && !filterStatus.isEmpty()) {
                getSupportActionBar().setTitle(filterStatus);
            } else {
                getSupportActionBar().setTitle("المفضلة");
            }
        }"""

content = content.replace(old_title_block, new_title_block)

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'w') as f:
    f.write(content)
