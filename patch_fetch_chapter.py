import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

new_fetch_chapter = """    public static void fetchChapterPages(String chapterUrl, ChapterPagesCallback callback) {
        new Thread(() -> {
            try {
                // 1. Fetch raw HTML instead of full DOM parsing
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(chapterUrl)
                        .build();
                okhttp3.Response response = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new Exception("HTTP " + response.code());
                }
                String html = response.body().string();

                List<String> imageUrls = new ArrayList<>();
                Set<String> uniqueUrls = new HashSet<>();

                // 2. Fast Regex extraction
                // This regex looks for <img> tags and captures src, data-src, data-lazy-src, etc.
                java.util.regex.Pattern imgPattern = java.util.regex.Pattern.compile(
                        "<img[^>]+(?:data-src|data-lazy-src|src)=[\"'](https?://[^\"']+\\.(?:jpg|jpeg|png|webp|gif|avif)[^\"']*)[\"'][^>]*>",
                        java.util.regex.Pattern.CASE_INSENSITIVE
                );
                java.util.regex.Matcher matcher = imgPattern.matcher(html);
                while (matcher.find()) {
                    String fullTag = matcher.group(0).toLowerCase(java.util.Locale.ROOT);
                    String url = matcher.group(1).trim();
                    
                    // Basic sanity check to avoid logos, headers, etc.
                    if (!fullTag.contains("logo") && !fullTag.contains("avatar") && !fullTag.contains("sidebar") && !url.contains("logo") && !url.contains("avatar") && !url.contains("icon")) {
                        // Check class attributes inside the tag
                        if (fullTag.contains("wp-manga-chapter-img") || fullTag.contains("chapter-img") || fullTag.contains("page-break")) {
                            if (!uniqueUrls.contains(url)) {
                                imageUrls.add(getHighResImageUrl(url));
                                uniqueUrls.add(url);
                            }
                        } else if (!uniqueUrls.contains(url) && isChapterPageImage(url, null)) {
                            imageUrls.add(getHighResImageUrl(url));
                            uniqueUrls.add(url);
                        }
                    }
                }

                // 3. Fallback: Parse with JSoup if regex missed images
                if (imageUrls.isEmpty()) {
                    Document doc = Jsoup.parse(html, chapterUrl);
                    String[] imgSelectors = {
                        "div.reading-content img", "div.page-break img", ".wp-manga-chapter-img",
                        "div.single-chapter img", ".reader-area img", "#readerarea img",
                        ".read-container img", ".chapter-content img", ".reading-content-wrap img"
                    };
                    for (String sel : imgSelectors) {
                        Elements images = doc.select(sel);
                        for (Element img : images) {
                            String url = extractImageUrlFromImgTag(img);
                            if (isChapterPageImage(url, img) && !uniqueUrls.contains(url)) {
                                imageUrls.add(getHighResImageUrl(url));
                                uniqueUrls.add(url);
                            }
                        }
                        if (imageUrls.size() >= 3) break;
                    }
                }

                // 4. JS array fallback (Preloaded images)
                if (imageUrls.isEmpty()) {
                    java.util.regex.Pattern jsArrayPattern = java.util.regex.Pattern.compile(
                            "\"(https?://[^\"]+\\.(?:jpg|jpeg|png|webp|gif|avif)[^\"]*)\"",
                            java.util.regex.Pattern.CASE_INSENSITIVE
                    );
                    // Match inside <script> blocks
                    java.util.regex.Matcher jsMatcher = jsArrayPattern.matcher(html);
                    while (jsMatcher.find()) {
                        String url = jsMatcher.group(1).replace("\\\\/", "/").trim();
                        if (isChapterPageImage(url, null) && !uniqueUrls.contains(url)) {
                            imageUrls.add(getHighResImageUrl(url));
                            uniqueUrls.add(url);
                        }
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!imageUrls.isEmpty()) callback.onSuccess(imageUrls);
                    else callback.onError("لم يتم العثور على صفحات الفصل.");
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("خطأ في تحميل الفصل: " + e.getMessage()));
            }
        }).start();
    }"""

pattern = r"public static void fetchChapterPages\(String chapterUrl, ChapterPagesCallback callback\) \{.*?\}\)\.start\(\);\s*\}"
content = re.sub(pattern, new_fetch_chapter.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)

