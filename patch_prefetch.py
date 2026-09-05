import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

new_prefetch = """
    private void prefetchNextChapter(String cookies) {
        if (nextChapterUrl == null || nextChapterUrl.isEmpty()) return;
        
        com.fire.mangareader.data.network.MangaScraper.fetchChapterPages(nextChapterUrl, new com.fire.mangareader.data.network.MangaScraper.ChapterPagesCallback() {
            @Override
            public void onSuccess(java.util.List<String> imageUrls) {
                // Prefetch first 3 images of next chapter
                int count = 0;
                for (String url : imageUrls) {
                    if (count >= 3) break;
                    com.bumptech.glide.Glide.with(getApplicationContext())
                        .load(url)
                        .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                        .preload();
                    count++;
                }
            }
            @Override
            public void onError(String errorMessage) { }
        });
    }
"""

pattern = r"private void prefetchNextChapter\(String cookies\) \{.*?(?=private void markChapterAsRead)"
content = re.sub(pattern, new_prefetch.strip() + "\n\n    ", content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(content)

