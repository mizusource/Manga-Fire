import re

with open("app/src/main/java/com/fire/mangareader/data/download/DownloadManager.java", "r") as f:
    text = f.read()

replacement = """
    // ADDED FOR COMPOSE COMPATIBILITY
    public void enqueueDownload(String chapterId, String mangaId, String mangaTitle, String chapterTitle) {
        startDownload(mangaId, chapterId, chapterTitle);
    }

    public kotlinx.coroutines.flow.Flow<java.util.List<com.fire.mangareader.domain.model.DownloadedChapter>> getDownloadsFlow() {
        return kotlinx.coroutines.flow.FlowKt.emptyFlow();
    }
    
    public void deleteDownload(String chapterId) {
        cancelDownload(chapterId);
    }
"""

# Replace the previous block we added
pattern = r'// ADDED FOR COMPOSE COMPATIBILITY.*?deleteDownload\(com\.fire\.mangareader\.domain\.model\.Chapter chapter\) \{\s*cancelDownload\(chapter\.getUrl\(\)\);\s*\}'

text = re.sub(pattern, replacement, text, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/data/download/DownloadManager.java", "w") as f:
    f.write(text)
