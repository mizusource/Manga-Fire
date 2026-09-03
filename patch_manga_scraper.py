filepath = 'app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java'
with open(filepath, 'r') as f:
    content = f.read()

new_method = """    public static void searchMangaPaginated(String query, int page, ScrapingCallback callback) {
        new Thread(() -> {
            try {
                String encodedQuery = query.replace(" ", "+");
                String[] possibleUrls;
                if (page <= 1) {
                    possibleUrls = new String[]{
                        BASE_URL + "?s=" + encodedQuery + "&post_type=wp-manga",
                        BASE_URL + "page/1/?s=" + encodedQuery + "&post_type=wp-manga",
                        BASE_URL + "search?q=" + encodedQuery
                    };
                } else {
                    possibleUrls = new String[]{
                        BASE_URL + "page/" + page + "/?s=" + encodedQuery + "&post_type=wp-manga",
                        BASE_URL + "search?q=" + encodedQuery + "&page=" + page
                    };
                }

                Document doc = null;
                String lastError = "";

                for (String url : possibleUrls) {
                    try {
                        doc = Jsoup.connect(url)
                                .userAgent(globalUserAgent)
                                .header("Cookie", globalCookies)
                                .referrer(BASE_URL)
                                .timeout(15000)
                                .get();
                        break;
                    } catch (org.jsoup.HttpStatusException e) {
                        if (e.getStatusCode() == 404) {
                            lastError = e.getMessage();
                            continue;
                        } else {
                            throw e;
                        }
                    } catch (Exception e) {
                        lastError = e.getMessage();
                        continue;
                    }
                }

                if (doc == null) {
                    throw new Exception("All search URLs returned 404: " + lastError);
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

content = content.replace('public static void searchManga(String query, ScrapingCallback callback) {', new_method + '\n    public static void searchManga(String query, ScrapingCallback callback) {')

with open(filepath, 'w') as f:
    f.write(content)
