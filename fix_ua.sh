sed -i 's/settings.setLoadsImagesAutomatically(false); settings.setBlockNetworkImage(true);/settings.setLoadsImagesAutomatically(true); settings.setBlockNetworkImage(false);/g' app/src/main/java/com/fire/mangareader/activity/MainActivity.java
sed -i 's/String agent = "Mozilla\/5.0 (Linux; Android 14; SM-A366B) AppleWebKit\/537.36 (KHTML, like Gecko) Chrome\/122.0.0.0 Mobile Safari\/537.36";/String agent = android.webkit.WebSettings.getDefaultUserAgent(this);/g' app/src/main/java/com/fire/mangareader/activity/MainActivity.java

sed -i 's/settings.setLoadsImagesAutomatically(false); settings.setBlockNetworkImage(true);/settings.setLoadsImagesAutomatically(true); settings.setBlockNetworkImage(false);/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/String agent = "Mozilla\/5.0 (Linux; Android 14; SM-A366B) AppleWebKit\/537.36 (KHTML, like Gecko) Chrome\/122.0.0.0 Mobile Safari\/537.36";/String agent = android.webkit.WebSettings.getDefaultUserAgent(this);/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java

sed -i 's/settings.setLoadsImagesAutomatically(false); settings.setBlockNetworkImage(true);/settings.setLoadsImagesAutomatically(true); settings.setBlockNetworkImage(false);/g' app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java
sed -i 's/String agent = "Mozilla\/5.0 (Linux; Android 14; SM-A366B) AppleWebKit\/537.36 (KHTML, like Gecko) Chrome\/122.0.0.0 Mobile Safari\/537.36";/String agent = android.webkit.WebSettings.getDefaultUserAgent(this);/g' app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java

sed -i 's/settings.setLoadsImagesAutomatically(false); settings.setBlockNetworkImage(true);/settings.setLoadsImagesAutomatically(true); settings.setBlockNetworkImage(false);/g' app/src/main/java/com/fire/mangareader/utils/MangaDownloader.java
