sed -i 's/webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);/webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);/g' app/src/main/java/com/fire/mangareader/activity/MainActivity.java
sed -i 's/webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);/webView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/scraperWebView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);/scraperWebView.setLayerType(android.view.View.LAYER_TYPE_SOFTWARE, null);/g' app/src/main/java/com/fire/mangareader/activity/ChapterReaderActivity.java
sed -i '/<application/a \        android:hardwareAccelerated="false"' app/src/main/AndroidManifest.xml
