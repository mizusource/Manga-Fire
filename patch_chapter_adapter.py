import os
import re

filepath = 'app/src/main/java/com/fire/mangareader/adapter/ChapterAdapter.java'
with open(filepath, 'r') as f:
    content = f.read()

# Replace manual folder checking with DownloadChecker in handleChapterExport
old_export_logic = """        // 1. فحص إذا كان الفصل محمل محلياً
        File mangaFolder = new File(context.getFilesDir(), String.valueOf(mangaUrl.hashCode()));
        File chapterFolder = new File(mangaFolder, String.valueOf(chapter.getUrl().hashCode()));
        List<String> pagePaths = new ArrayList<>();
        if (chapterFolder.exists() && chapterFolder.isDirectory()) {
            File[] files = chapterFolder.listFiles();
            if (files != null && files.length > 0) {
                java.util.Arrays.sort(files, (f1, f2) -> f1.getName().compareTo(f2.getName()));
                for (File f : files) {
                    if (f.isFile() && (f.getName().endsWith(".jpg") || f.getName().endsWith(".png") || f.getName().endsWith(".webp"))) {
                        pagePaths.add(f.getAbsolutePath());
                    }
                }
            }
        }"""

new_export_logic = """        // 1. فحص إذا كان الفصل محمل محلياً باستخدام DownloadChecker
        List<String> pagePaths = new ArrayList<>();
        if (com.fire.mangareader.utils.DownloadChecker.isChapterDownloaded(context, mangaUrl, chapter.getUrl())) {
            List<File> images = com.fire.mangareader.utils.DownloadChecker.getDownloadedImages(context, mangaUrl, chapter.getUrl());
            for (File img : images) {
                pagePaths.add(img.getAbsolutePath());
            }
        }"""

content = content.replace(old_export_logic, new_export_logic)

# Replace local folder check inside onSuccess callback for DownloadListener
old_success_logic = """                @Override
                public void onSuccess() {
                    File[] downloadedFiles = chapterFolder.listFiles();
                    List<String> dPaths = new ArrayList<>();
                    if (downloadedFiles != null) {
                        java.util.Arrays.sort(downloadedFiles, (f1, f2) -> f1.getName().compareTo(f2.getName()));
                        for (File f : downloadedFiles) {
                            if (f.isFile()) dPaths.add(f.getAbsolutePath());
                        }
                    }
                    com.fire.mangareader.utils.ChapterExportManager.exportChapter(context, mangaTitle, chapter.getTitle(), chapter.getUrl(), dPaths, format, "ORIGINAL", callback);
                }"""

new_success_logic = """                @Override
                public void onSuccess() {
                    List<String> dPaths = new ArrayList<>();
                    List<File> downloadedFiles = com.fire.mangareader.utils.DownloadChecker.getDownloadedImages(context, mangaUrl, chapter.getUrl());
                    for (File f : downloadedFiles) {
                        dPaths.add(f.getAbsolutePath());
                    }
                    com.fire.mangareader.utils.ChapterExportManager.exportChapter(context, mangaTitle, chapter.getTitle(), chapter.getUrl(), dPaths, format, "ORIGINAL", callback);
                }"""

content = content.replace(old_success_logic, new_success_logic)

# Make isDownloaded smarter
old_is_down = "boolean isDownloaded = downloadedChapters != null && downloadedChapters.contains(chapter.getUrl());"
new_is_down = "boolean isDownloaded = (downloadedChapters != null && downloadedChapters.contains(chapter.getUrl())) || com.fire.mangareader.utils.DownloadChecker.isChapterDownloaded(context, mangaUrl, chapter.getUrl());"
content = content.replace(old_is_down, new_is_down)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched ChapterAdapter")
