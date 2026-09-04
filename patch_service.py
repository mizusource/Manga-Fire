import re

with open("app/src/main/java/com/fire/mangareader/data/service/DownloadService.java", "r") as f:
    text = f.read()

# Add import for DownloadModule
if "com.fire.mangareader.core.di.DownloadModule" not in text:
    text = text.replace("import com.fire.mangareader.util.MangaDownloader;", "import com.fire.mangareader.util.MangaDownloader;\nimport com.fire.mangareader.core.di.DownloadModule;\nimport com.fire.mangareader.data.download.DownloadManager;")

# Inside onProgressUpdate
progress_update_pattern = r'public void onProgressUpdate\(int current, int total\) \{\s+if \(MangaDownloader\.isCancelled\) return;'
progress_update_replacement = r'''public void onProgressUpdate(int current, int total) {
                    if (MangaDownloader.isCancelled) return;
                    int progress = (int) (((float) current / total) * 100);
                    DownloadModule.getInstance(DownloadService.this).provideDownloadManager().updateProgress(task.chapterUrl, progress);
'''
text = re.sub(progress_update_pattern, progress_update_replacement, text)

# Inside onSuccess
success_pattern = r'public void onSuccess\(\) \{\s+if \(MangaDownloader\.isCancelled\) return;'
success_replacement = r'''public void onSuccess() {
                    if (MangaDownloader.isCancelled) return;
                    DownloadModule.getInstance(DownloadService.this).provideDownloadManager().removeProgress(task.chapterUrl);
'''
text = re.sub(success_pattern, success_replacement, text)

# Inside onError
error_pattern = r'public void onError\(String errorMessage\) \{\s+if \(MangaDownloader\.isCancelled\) return;'
error_replacement = r'''public void onError(String errorMessage) {
                    if (MangaDownloader.isCancelled) return;
                    DownloadModule.getInstance(DownloadService.this).provideDownloadManager().removeProgress(task.chapterUrl);
'''
text = re.sub(error_pattern, error_replacement, text)

with open("app/src/main/java/com/fire/mangareader/data/service/DownloadService.java", "w") as f:
    f.write(text)
