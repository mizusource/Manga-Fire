with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

replacement = """
    public static List<Manga> searchSingleSourcePaginated(String sourceUrl, String query, java.util.List<String> genres, String status, String type, int page) throws Exception {
        String encodedQuery = query.replace(" ", "+");
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(sourceUrl).append("page/").append(page).append("/?s=").append(encodedQuery).append("&post_type=wp-manga");
        
        if (genres != null) {
            for (String genre : genres) {
                urlBuilder.append("&genre[]=").append(java.net.URLEncoder.encode(genre, "UTF-8"));
            }
        }
        if (status != null && !status.isEmpty() && !status.equals("الكل")) urlBuilder.append("&status[]=").append(status);
        if (type != null && !type.isEmpty() && !type.equals("الكل")) urlBuilder.append("&op-1=1&author=&artist=&release=&adult=");

        Document doc = Jsoup.connect(urlBuilder.toString())
                .userAgent(globalUserAgent)
                .header("Cookie", globalCookies)
                .referrer(sourceUrl)
                .timeout(15000)
                .get();

        List<Manga> mangaList = new ArrayList<>();
        Set<String> uniqueUrls = new HashSet<>();
        
        Elements mangaElements = doc.select(".c-tabs-item__content, .page-item-detail");
        
        for (Element element : mangaElements) {
            Manga manga = new Manga();
            Element titleElement = element.select("h3 a, .post-title a").first();
            if (titleElement != null) {
                manga.setTitle(titleElement.text().trim());
                manga.setUrl(titleElement.absUrl("href"));
            }
            if (manga.getUrl() != null && uniqueUrls.contains(manga.getUrl())) continue;
            
            Element imgElement = element.select(".tab-thumb img, .item-thumb img").first();
            if (imgElement != null) {
                manga.setCoverUrl(extractImageUrlFromImgTag(imgElement));
            }
            
            Element chapterElement = element.select(".chapter-item .chapter, .list-chapter .chapter, .font-meta").first();
            if (chapterElement != null) {
                manga.setLatestChapter(chapterElement.text().trim());
            }
            
            Element ratingElement = element.select(".score").first();
            if (ratingElement != null) {
                manga.setRating(ratingElement.text().trim());
            }
            
            if (manga.getUrl() != null && !manga.getUrl().isEmpty()) {
                uniqueUrls.add(manga.getUrl());
                mangaList.add(manga);
            }
        }
        return mangaList;
    }

    public static void searchAdvancedPaginated(String query, java.util.List<String> genres, String status, String type, int page, ScrapingCallback callback) {
        new Thread(() -> {
            String[] sources = SourceManager.getAllSources();
            List<Manga> combinedList = new ArrayList<>();
            Set<String> addedUrls = new HashSet<>();
            java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(sources.length);
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(sources.length);
            
            for (String src : sources) {
                executor.execute(() -> {
                    try {
                        List<Manga> res = searchSingleSourcePaginated(src, query, genres, status, type, page);
                        synchronized (combinedList) {
                            for (Manga m : res) {
                                if (m.getUrl() != null && !addedUrls.contains(m.getUrl())) {
                                    addedUrls.add(m.getUrl());
                                    combinedList.add(m);
                                }
                            }
                        }
                    } catch(Exception ignored) {} finally {
                        latch.countDown();
                    }
                });
            }
            
            try {
                latch.await(15, java.util.concurrent.TimeUnit.SECONDS);
            } catch (Exception ignored) {}
            executor.shutdown();
            
            new Handler(Looper.getMainLooper()).post(() -> {
                if (!combinedList.isEmpty()) callback.onSuccess(combinedList);
                else callback.onError("لم يتم العثور على أي نتائج إضافية.");
            });
        }).start();
    }
"""

# Find where searchAdvancedPaginated is and replace it entirely
import re
pattern = re.compile(r"public static void searchAdvancedPaginated.*?\}\)\.start\(\);\n    \}", re.DOTALL)
content = pattern.sub(replacement.strip(), content)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)
