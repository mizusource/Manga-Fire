import re

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/ChapterAdapter.java", "r") as f:
    text = f.read()

# Add import
if "com.fire.mangareader.core.di.DownloadModule" not in text:
    text = text.replace("import com.fire.mangareader.data.service.DownloadService;", "import com.fire.mangareader.data.service.DownloadService;\nimport com.fire.mangareader.core.di.DownloadModule;\nimport com.fire.mangareader.data.download.DownloadManager;")

# Replace startDownload
text = text.replace("DownloadService.startDownload(context, mangaUrl, chapter.getUrl(), chapter.getTitle());", "DownloadModule.getInstance(context).provideDownloadManager().startDownload(mangaUrl, chapter.getUrl(), chapter.getTitle());")

with open("app/src/main/java/com/fire/mangareader/presentation/adapter/ChapterAdapter.java", "w") as f:
    f.write(text)
