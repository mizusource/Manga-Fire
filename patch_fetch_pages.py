import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

# We will rewrite fetchChapterPages to use the DynamicParserEngine.

old_method_pattern = r'public static void fetchChapterPages\(String chapterUrl, ChapterPagesCallback callback\) \{.*?(?=\n    public static void fetchSourceList)'

new_method = """public static void fetchChapterPages(String chapterUrl, ChapterPagesCallback callback) {
        new Thread(() -> {
            try {
                // Get HTML string via OkHttp (DirectIpInterceptor is active here!)
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(chapterUrl)
                        .build();
                okhttp3.Response response = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(request).execute();
                if (!response.isSuccessful()) {
                    throw new Exception("HTTP " + response.code());
                }
                String html = response.body().string();

                // 🚀 Configure the DynamicParserEngine for fetching pages
                com.fire.mangareader.data.parser.DynamicParserEngine engine = new com.fire.mangareader.data.parser.DynamicParserEngine();
                
                // Define the selectors equivalent to what was used
                java.util.Map<String, com.fire.mangareader.domain.model.parser.SelectorConfig> selectors = new java.util.HashMap<>();
                selectors.put("url", new com.fire.mangareader.domain.model.parser.SelectorConfig("", "data-src", null));
                
                // Clean-up transformations to make sure we get high-res HTTP URLs
                java.util.List<com.fire.mangareader.domain.model.parser.UrlTransformation> transforms = new java.util.ArrayList<>();
                transforms.add(new com.fire.mangareader.domain.model.parser.UrlTransformation(
                        com.fire.mangareader.domain.model.parser.TransformationType.REGEX_REPLACE, 
                        "\\\\b(175|350|w190|w320)\\\\b", "")); // Remove low res constraints
                transforms.add(new com.fire.mangareader.domain.model.parser.UrlTransformation(
                        com.fire.mangareader.domain.model.parser.TransformationType.SUBDOMAIN_REPLACE, 
                        "^//", "https://")); // fix missing protocol
                
                // Construct Extractor Config
                com.fire.mangareader.domain.model.parser.ExtractorConfig config = new com.fire.mangareader.domain.model.parser.ExtractorConfig(
                        "PagesExtractor",
                        com.fire.mangareader.domain.model.parser.ExtractorType.PAGES,
                        "div.reading-content img, div.page-break img, .wp-manga-chapter-img, div.single-chapter img, .reader-area img, #readerarea img, .read-container img, .chapter-content img, .reading-content-wrap img, div.entry-content img, div.entry-content p img, div.text-center img, div.text-left img, div[id*='chapter'] img, div[class*='chapter'] img, .main-col img, div.post-content img, .chapter-image img",
                        new java.util.HashMap<>(),
                        new java.util.ArrayList<>(),
                        selectors,
                        com.fire.mangareader.domain.model.parser.ResponseFormat.HTML,
                        transforms
                );

                // Run Parser
                java.util.List<String> parsedPages = engine.parsePages(html, config);
                
                // Fallback for "src" if "data-src" was empty, because DynamicParser currently maps purely to what we define.
                // We could also refine the CSS selector in the config to prefer src, but let's just make it robust.
                java.util.List<String> finalUrls = new java.util.ArrayList<>();
                if (parsedPages.isEmpty()) {
                     // Generic fallback just in case
                     config = config.copy(
                             config.getName(), config.getType(), "img", config.getFields(), config.getParameters(),
                             java.util.Collections.singletonMap("url", new com.fire.mangareader.domain.model.parser.SelectorConfig("", "src", null)),
                             config.getResponse_format(), config.getUrl_transformations()
                     );
                     parsedPages = engine.parsePages(html, config);
                }

                // Filter valid image urls
                for (String url : parsedPages) {
                    if (url != null && !url.trim().isEmpty() && !url.endsWith(".gif")) {
                        // Applying high res formatting manually for now to preserve existing structure
                        finalUrls.add(getHighResImageUrl(url.trim()));
                    }
                }

                if (finalUrls.isEmpty()) {
                    throw new Exception("No pages found.");
                }

                if (callback != null) {
                    callback.onSuccess(finalUrls);
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        }).start();
    }"""

content = re.sub(old_method_pattern, new_method, content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)
print("fetchChapterPages Replaced with Dynamic Engine!")
