    public static void searchManga(String query, ScrapingCallback callback) {
        new Thread(() -> {
            try {
                String[] possibleUrls = {
                    BASE_URL + "?s=" + query.replace(" ", "+") + "&post_type=wp-manga",
                    BASE_URL + "?s=" + query.replace(" ", "+"),
                    BASE_URL + "search?q=" + query.replace(" ", "+"),
                    BASE_URL + "page/1/?s=" + query.replace(" ", "+") + "&post_type=wp-manga"
                };

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
                        break; // Success
                    } catch (org.jsoup.HttpStatusException e) {
                        if (e.getStatusCode() == 404) {
                            lastError = e.getMessage();
                            continue; // Try next URL
                        } else {
                            throw e;
                        }
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
                        String imgUrl = imgElement.hasAttr("data-src") ? imgElement.absUrl("data-src") : imgElement.absUrl("src");
                        manga.setCoverUrl(imgUrl);
                    }

                    if (manga.getTitle() != null && !manga.getTitle().isEmpty() && manga.getUrl() != null) {
                        mangaList.add(manga);
                        uniqueUrls.add(manga.getUrl());
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!mangaList.isEmpty()) callback.onSuccess(mangaList);
                    else callback.onError("لم يتم العثور على نتائج للبحث.");
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("خطأ في البحث: " + e.getMessage()));
            }
        }).start();
    }
