    private static void downloadImageFile(Context context, String fileUrl, String chapterUrl, File outputFile) throws Exception {
        fileUrl = fileUrl.trim().replace(" ", "%20");
        if (fileUrl.startsWith("//")) {
            fileUrl = "https:" + fileUrl;
        }
        
        android.content.SharedPreferences prefs = context.getSharedPreferences("MangaFirePrefs", Context.MODE_PRIVATE);
        int quality = prefs.getInt("image_quality_value", 100);
        
        String requestUrl = fileUrl;
        
        if (quality < 100) {
            String encodedUrl = java.net.URLEncoder.encode(fileUrl, "UTF-8");
            requestUrl = "https://wsrv.nl/?url=" + encodedUrl + "&q=" + quality + "&output=webp";
        }

        Request request = new Request.Builder()
                .url(requestUrl)
                .header("Referer", chapterUrl) // مهم جداً لتخطي حظر الصور
                .build();

        // تمرير الطلب لـ OkHttp الذي يملك كل الكوكيز والحيل
        Response response = MangaOkHttp.getClient().newCall(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("HTTP Error: " + response.code());
        }

        byte[] imageBytes = response.body().bytes();
        
        FileOutputStream fos = new FileOutputStream(outputFile);
        fos.write(imageBytes);
        fos.flush();
        fos.close();
    }
