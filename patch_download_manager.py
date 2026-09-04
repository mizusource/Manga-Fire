import re

with open("app/src/main/java/com/fire/mangareader/data/download/DownloadManager.java", "r") as f:
    text = f.read()

replacement = """
    // ADDED FOR COMPOSE COMPATIBILITY
    public void enqueueDownload(com.fire.mangareader.domain.model.Manga manga, com.fire.mangareader.domain.model.Chapter chapter) {
        startDownload(manga.getUrl(), chapter.getUrl(), chapter.getTitle());
    }

    public kotlinx.coroutines.flow.Flow<java.util.List<com.fire.mangareader.domain.model.Chapter>> getDownloadsFlow() {
        return kotlinx.coroutines.flow.FlowKt.emptyFlow();
    }
    
    public void deleteDownload(com.fire.mangareader.domain.model.Chapter chapter) {
        cancelDownload(chapter.getUrl());
    }
"""

text = text.replace("public DownloadManager(Context context) {", replacement + "\n    public DownloadManager(Context context) {")

with open("app/src/main/java/com/fire/mangareader/data/download/DownloadManager.java", "w") as f:
    f.write(text)
