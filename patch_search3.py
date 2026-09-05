import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

new_search = """    public static List<Manga> searchSingleSourcePaginated(String sourceUrl, String query, java.util.List<String> genres, String status, String type, int page) throws Exception {
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

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(urlBuilder.toString())
                .addHeader("User-Agent", globalUserAgent)
                .addHeader("Cookie", globalCookies)
                .addHeader("Referer", sourceUrl)
                .build();
                
        okhttp3.Response response = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(request).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("HTTP " + response.code());
        }
        
        String html = response.body().string();
        Document doc = Jsoup.parse(html, urlBuilder.toString());

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
    }"""

pattern = r"public static List<Manga> searchSingleSourcePaginated\(String sourceUrl,.*?return mangaList;\s*\}"
content = re.sub(pattern, new_search.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)

