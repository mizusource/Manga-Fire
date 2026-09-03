import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

old_method_pattern = r'private void prefetchNextChapter\(String cookies\) \{.*?\}\s*\}\)\.start\(\);\s*\}'

new_method = """private void prefetchNextChapter(String cookies) {
        if (nextChapterUrl == null || nextChapterUrl.isEmpty()) return;
        new Thread(() -> {
            try {
                // Use MangaOkHttp with DirectIpInterceptor
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(nextChapterUrl)
                        .header("Cookie", cookies != null ? cookies : com.fire.mangareader.data.network.MangaScraper.globalCookies)
                        .header("Referer", chapterUrl != null ? chapterUrl : nextChapterUrl)
                        .header("User-Agent", com.fire.mangareader.data.network.MangaScraper.globalUserAgent)
                        .build();
                        
                okhttp3.Response response = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(request).execute();
                if (!response.isSuccessful()) return;
                
                String html = response.body().string();
                
                // 🚀 Configure DynamicParserEngine
                com.fire.mangareader.data.parser.DynamicParserEngine engine = new com.fire.mangareader.data.parser.DynamicParserEngine();
                java.util.Map<String, com.fire.mangareader.domain.model.parser.SelectorConfig> selectors = new java.util.HashMap<>();
                selectors.put("url", new com.fire.mangareader.domain.model.parser.SelectorConfig("", "data-src", null));
                
                java.util.List<com.fire.mangareader.domain.model.parser.UrlTransformation> transforms = new java.util.ArrayList<>();
                transforms.add(new com.fire.mangareader.domain.model.parser.UrlTransformation(
                        com.fire.mangareader.domain.model.parser.TransformationType.SUBDOMAIN_REPLACE, 
                        "^//", "https://"));
                        
                com.fire.mangareader.domain.model.parser.ExtractorConfig config = new com.fire.mangareader.domain.model.parser.ExtractorConfig(
                        "PagesPrefetch",
                        com.fire.mangareader.domain.model.parser.ExtractorType.PAGES,
                        ".reading-content img, .page-break img, #vungdoc img, .vung-doc img, .chapter-video-frame img",
                        new java.util.HashMap<>(),
                        new java.util.ArrayList<>(),
                        selectors,
                        com.fire.mangareader.domain.model.parser.ResponseFormat.HTML,
                        transforms
                );
                
                java.util.List<String> parsedPages = engine.parsePages(html, config);
                if (parsedPages.isEmpty()) {
                     config = config.copy(
                             config.getName(), config.getType(), "img", config.getFields(), config.getParameters(),
                             java.util.Collections.singletonMap("url", new com.fire.mangareader.domain.model.parser.SelectorConfig("", "src", null)),
                             config.getResponse_format(), config.getUrl_transformations()
                     );
                     parsedPages = engine.parsePages(html, config);
                }

                int count = 0;
                for (String imgUrl : parsedPages) {
                    if (count >= 4) break;
                    if (!imgUrl.isEmpty() && !imgUrl.endsWith(".gif")) {
                        java.io.File tempCacheFile = new java.io.File(getCacheDir(), "img_" + Math.abs(imgUrl.hashCode()) + ".jpg");
                        if (!tempCacheFile.exists()) {
                            okhttp3.Request req = new okhttp3.Request.Builder()
                                    .url(imgUrl)
                                    .header("Referer", nextChapterUrl)
                                    .build();
                            okhttp3.Response resp = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(req).execute();
                            if (resp.isSuccessful() && resp.body() != null) {
                                java.io.FileOutputStream fos = new java.io.FileOutputStream(tempCacheFile);
                                fos.write(resp.body().bytes());
                                fos.flush();
                                fos.close();
                            }
                        }
                        count++;
                    }
                }
            } catch (Exception ignored) {}
        }).start();
    }"""

content = re.sub(old_method_pattern, new_method, content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(content)
print("fetchChapterPages Replaced with Dynamic Engine in Reader!")
