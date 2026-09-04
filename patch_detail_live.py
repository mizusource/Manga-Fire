import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    text = f.read()

# Make MangaDetailActivity observe LiveData
if "DownloadModule.getInstance(this).provideDownloadManager().getDownloadProgress()" not in text:
    observe_code = """
        // Observe active downloads
        DownloadModule.getInstance(this).provideDownloadManager().getDownloadProgress().observe(this, progresses -> {
            if (chapterAdapter != null) {
                chapterAdapter.setDownloadProgresses(progresses);
            }
        });
"""
    # Find onCreate
    oncreate_pattern = r'protected void onCreate\(Bundle savedInstanceState\) \{[\s\S]*?super\.onCreate\(savedInstanceState\);'
    text = re.sub(oncreate_pattern, lambda m: m.group(0) + observe_code, text)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(text)
