import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

new_fetch_latest = """    public static void fetchLatestManga(ScrapingCallback callback) {
        if (BASE_URL.contains("dilar.tube")) {
            DilarScraper.fetchLatestManga(callback);
            return;
        }

        new Thread(() -> {
            try {
                List<Manga> mangaList;
                if (BASE_URL.contains("mangalik.net")) {
                    mangaList = fetchLatestFromAjax(BASE_URL);
                } else {
                    Document doc = getDocument(BASE_URL);
                    mangaList = new ArrayList<>();
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
    
                        Element imgElement = element.select(".item-thumb img, .post-title img").first();
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
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onSuccess(mangaList);
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onError("خطأ في جلب البيانات: " + e.getMessage());
                });
            }
        }).start();
    }"""

pattern = r"public static void fetchLatestManga\(ScrapingCallback callback\) \{.*?\}\)\.start\(\);\s*\}"
content = re.sub(pattern, new_fetch_latest, content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)

