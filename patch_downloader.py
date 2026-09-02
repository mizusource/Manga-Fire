import re

filepath = 'app/src/main/java/com/fire/mangareader/util/MangaDownloader.java'
with open(filepath, 'r') as f:
    content = f.read()

# Replace the downloadImageFile to use .tmp files
new_method = """    private static void downloadImageFile(Context context, String fileUrl, String chapterUrl, java.io.File outputFile) throws Exception {
        fileUrl = fileUrl.trim().replace(" ", "%20");
        if (fileUrl.startsWith("//")) {
            fileUrl = "https:" + fileUrl;
        }
        
        android.content.SharedPreferences defaultPrefs = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context);
        String qualityKey = defaultPrefs.getString("download_quality", "MEDIUM");
        com.fire.mangareader.domain.model.DownloadQuality qualityEnum;
        try {
            qualityEnum = com.fire.mangareader.domain.model.DownloadQuality.valueOf(qualityKey);
        } catch (Exception e) {
            qualityEnum = com.fire.mangareader.domain.model.DownloadQuality.MEDIUM;
        }

        // استخدام ملف مؤقت لمنع حفظ صور تالفة عند انقطاع الاتصال
        java.io.File tmpFile = new java.io.File(outputFile.getAbsolutePath() + ".tmp");

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(fileUrl)
                .header("Referer", chapterUrl) // مهم جداً لتخطي حظر الصور
                .build();
        okhttp3.Response response = MangaOkHttp.getClient().newCall(request).execute();

        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("HTTP Error: " + response.code());
        }

        byte[] imageBytes = response.body().bytes();

        if (qualityEnum == com.fire.mangareader.domain.model.DownloadQuality.HIGH) {
            java.io.FileOutputStream fos = new java.io.FileOutputStream(tmpFile);
            fos.write(imageBytes);
            fos.flush();
            fos.close();
        } else {
            try {
                android.graphics.Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                if (bitmap != null) {
                    int originalWidth = bitmap.getWidth();
                    int originalHeight = bitmap.getHeight();
                    int maxW = qualityEnum.getMaxPixelWidth();
                    
                    if (originalWidth > maxW) {
                        int targetHeight = (int) (((float) originalHeight / originalWidth) * maxW);
                        android.graphics.Bitmap scaled = android.graphics.Bitmap.createScaledBitmap(bitmap, maxW, targetHeight, true);
                        if (scaled != bitmap) {
                            bitmap.recycle();
                            bitmap = scaled;
                        }
                    }
                    
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(tmpFile);
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, qualityEnum.getCompressionQuality(), fos);
                    fos.flush();
                    fos.close();
                    bitmap.recycle();
                } else {
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(tmpFile);
                    fos.write(imageBytes);
                    fos.flush();
                    fos.close();
                }
            } catch (Throwable t) {
                java.io.FileOutputStream fos = new java.io.FileOutputStream(tmpFile);
                fos.write(imageBytes);
                fos.flush();
                fos.close();
            }
        }
        
        // بمجرد التأكد من كتابة الملف بنجاح دون أخطاء، نقوم بإعادة تسميته إلى الاسم الأصلي (.jpg)
        if (tmpFile.exists()) {
            if (outputFile.exists()) {
                outputFile.delete();
            }
            tmpFile.renameTo(outputFile);
        }
    }
"""

content = re.sub(r'    private static void downloadImageFile\(Context context, String fileUrl, String chapterUrl, File outputFile\) throws Exception \{.*?\}\n    \}\n\}', new_method + '}\n', content, flags=re.DOTALL)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MangaDownloader.java")
