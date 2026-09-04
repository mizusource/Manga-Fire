import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    text = f.read()

# Add import
if "com.fire.mangareader.core.di.DownloadModule" not in text:
    text = text.replace("import com.fire.mangareader.presentation.adapter.ChapterAdapter;", "import com.fire.mangareader.presentation.adapter.ChapterAdapter;\nimport com.fire.mangareader.core.di.DownloadModule;\nimport com.fire.mangareader.data.download.DownloadManager;")

# Replace startDownload
text = text.replace("com.fire.mangareader.data.service.DownloadService.startDownload(this, mangaUrl, c.getUrl(), c.getTitle());", "DownloadModule.getInstance(this).provideDownloadManager().startDownload(mangaUrl, c.getUrl(), c.getTitle());")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(text)
