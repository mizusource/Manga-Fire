import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

# I want to rewrite fetchLatestSingleSource to use admin-ajax.php
new_fetch_single_source = """
    public static List<Manga> fetchLatestSingleSource(String sourceUrl) throws Exception {
        // If sourceUrl is mangalik.net, we can use the fast AJAX endpoint
        if (sourceUrl.contains("mangalik.net")) {
            return fetchLatestFromAjax(sourceUrl);
        }
        
        // Fallback for other sources
        Document doc = getDocument(sourceUrl);
        List<Manga> mangaList = new ArrayList<>();
        Set<String> uniqueUrls = new HashSet<>();

        Elements mangaElements = doc.select(".page-item-detail");
        for (Element element : mangaElements) {
            Manga manga = new Manga();

            Element titleElement = element.select("h3 a, .post-title a").first();
            if (titleElement != null) {
                manga.setTitle(titleElement.text().trim());
                manga.setUrl(titleElement.absUrl("href"));
            }

            if (manga.getUrl() != null && uniqueUrls.contains(manga.getUrl())) continue;

            Element imgElement = element.select(".tab-thumb img, .item-thumb img, .post-title img").first();
            if (imgElement != null) {
                manga.setCoverUrl(extractImageUrlFromImgTag(imgElement));
            }

            Element chapterElement = element.select(".chapter-item .chapter, .list-chapter .chapter").first();
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

    private static List<Manga> fetchLatestFromAjax(String baseUrl) throws Exception {
        okhttp3.OkHttpClient client = com.fire.mangareader.util.MangaOkHttp.getClient();
        
        // Build the URL for admin-ajax.php
        String ajaxUrl = baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "wp-admin/admin-ajax.php";
        
        okhttp3.RequestBody requestBody = new okhttp3.FormBody.Builder()
                .add("action", "madara_load_more")
                .add("page", "0")
                .add("template", "madara-core/content/content-archive")
                .add("vars[paged]", "1")
                .add("vars[orderby]", "meta_value_num")
                .add("vars[template]", "archive")
                .add("vars[sidebar]", "full")
                .add("vars[post_type]", "wp-manga")
                .add("vars[post_status]", "publish")
                .add("vars[meta_key]", "_latest_update")
                .add("vars[order]", "desc")
                .build();
                
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(ajaxUrl)
                .post(requestBody)
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("User-Agent", globalUserAgent)
                .addHeader("Cookie", globalCookies)
                .build();
                
        okhttp3.Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new Exception("HTTP " + response.code());
        }
        
        String html = response.body().string();
        Document doc = Jsoup.parse(html, baseUrl);
        
        List<Manga> mangaList = new ArrayList<>();
        Set<String> uniqueUrls = new HashSet<>();

        Elements mangaElements = doc.select(".page-item-detail");
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

            Element chapterElement = element.select(".chapter-item .chapter, .list-chapter .chapter").first();
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
"""

pattern = r"public static List<Manga> fetchLatestSingleSource\(String sourceUrl\) throws Exception \{.*?return mangaList;\s*\}"
content = re.sub(pattern, new_fetch_single_source, content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)

