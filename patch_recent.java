        String mangaTitle = getIntent().getStringExtra("mangaTitle");
        String mangaCover = getIntent().getStringExtra("mangaCover");

        if (mangaTitle != null && mangaCover != null) {
            com.fire.mangareader.utils.RecentReadingManager.RecentItem item = new com.fire.mangareader.utils.RecentReadingManager.RecentItem();
            item.mangaUrl = mangaUrl;
            item.mangaTitle = mangaTitle;
            item.mangaCover = mangaCover;
            item.chapterUrl = chapterUrl;
            item.chapterTitle = chapterTitle;
            com.fire.mangareader.utils.RecentReadingManager.addRecent(this, item);
        }
