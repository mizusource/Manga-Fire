import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

new_search = """    public static void searchMangaPaginated(String query, int page, ScrapingCallback callback) {
        new Thread(() -> {
            try {
                String encodedQuery = query.replace(" ", "+");
                String searchUrl = page <= 1 
                        ? BASE_URL + "?s=" + encodedQuery + "&post_type=wp-manga"
                        : BASE_URL + "page/" + page + "/?s=" + encodedQuery + "&post_type=wp-manga";

                Document doc = getDocument(searchUrl);
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

                    Element imgElement = element.select(".tab-thumb img, .item-thumb img, .post-title img").first();
                    if (imgElement != null) {
                        manga.setCoverUrl(extractImageUrlFromImgTag(imgElement));
                    }

                    Element chapterElement = element.select(".font-meta.chapter, .list-chapter .chapter").first();
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

                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(mangaList);
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onError("All search URLs returned 404: " + e.getMessage());
                });
            }
        }).start();
    }"""

pattern = r"public static void searchMangaPaginated\(String query, int page, ScrapingCallback callback\) \{.*?\}\)\.start\(\);\s*\}"
content = re.sub(pattern, new_search.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)

