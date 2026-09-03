import re

filepath = 'app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java'
with open(filepath, 'r') as f:
    content = f.read()

new_method = """    public static void searchAdvancedPaginated(String query, java.util.List<String> genres, String status, String type, int page, ScrapingCallback callback) {
        new Thread(() -> {
            try {
                String encodedQuery = query.replace(" ", "+");
                StringBuilder urlBuilder = new StringBuilder();
                urlBuilder.append(BASE_URL).append("page/").append(page).append("/?s=").append(encodedQuery).append("&post_type=wp-manga");

                if (genres != null) {
                    for (String genre : genres) {
                        urlBuilder.append("&genre[]=").append(java.net.URLEncoder.encode(genre, "UTF-8"));
                    }
                }
                if (status != null && !status.isEmpty()) {
                    urlBuilder.append("&status[]=").append(status);
                }
                if (type != null && !type.isEmpty()) {
                    urlBuilder.append("&op-manga_type[]=").append(type);
                }

                String targetUrl = urlBuilder.toString();
                Document doc = null;
                String lastError = "";

                try {
                    doc = Jsoup.connect(targetUrl)
                            .userAgent(globalUserAgent)
                            .header("Cookie", globalCookies)
                            .referrer(BASE_URL)
                            .timeout(15000)
                            .get();
                } catch (Exception e) {
                    lastError = e.getMessage();
                }

                if (doc == null) {
                    throw new Exception("Advanced search failed: " + lastError);
                }

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
                    if (imgElement == null) imgElement = element.select("img").first();
                    if (imgElement != null) {
                        manga.setCoverUrl(imgElement.hasAttr("data-src") ? imgElement.absUrl("data-src") : imgElement.absUrl("src"));
                    }

                    Element latestChapter = element.select(".chapter a, .list-chapter a, .font-meta").first();
                    if (latestChapter != null) {
                        manga.setLatestChapter(latestChapter.text().trim());
                    }

                    Element ratingElement = element.select(".score, .numscore, .post-total-rating .score").first();
                    if (ratingElement != null) {
                        manga.setRating(ratingElement.text().trim());
                    }

                    if (manga.getTitle() != null && !manga.getTitle().isEmpty()) {
                        mangaList.add(manga);
                        if (manga.getUrl() != null) uniqueUrls.add(manga.getUrl());
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (callback != null) callback.onSuccess(mangaList);
                });

            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (callback != null) callback.onError(e.getMessage());
                });
            }
        }).start();
    }
"""

content = content.replace('public static void searchMangaPaginated(String query, int page, ScrapingCallback callback) {', new_method + '\n    public static void searchMangaPaginated(String query, int page, ScrapingCallback callback) {')

with open(filepath, 'w') as f:
    f.write(content)
print("Added searchAdvancedPaginated")
