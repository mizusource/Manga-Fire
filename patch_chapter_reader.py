import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Replace the Toast in checkIfChapterIsDownloadedAndLoad to use safeToast
content = content.replace('Toast.makeText(this, "رابط الفصل غير صالح", Toast.LENGTH_SHORT).show();', 'com.fire.mangareader.util.SystemUtils.safeToast(this, "رابط الفصل غير صالح");')

# Add network check before setupWebViewScraper()
target_logic = """                        if (!com.fire.mangareader.util.SystemUtils.isNetworkAvailable(ChapterReaderActivity.this)) {
                            runOnUiThread(() -> {
                                loadingProgressBar.setVisibility(View.GONE);
                                com.fire.mangareader.util.SystemUtils.safeToast(ChapterReaderActivity.this, "لا يوجد اتصال بالإنترنت");
                            });
                            return;
                        }
                        
                        runOnUiThread(() -> setupWebViewScraper());"""

content = content.replace('runOnUiThread(() -> setupWebViewScraper());', target_logic)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched ChapterReaderActivity.java")
