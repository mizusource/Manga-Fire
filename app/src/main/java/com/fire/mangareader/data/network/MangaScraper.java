package com.fire.mangareader.data.network;

import android.os.Handler;
import android.os.Looper;
import com.fire.mangareader.domain.model.Chapter;
import com.fire.mangareader.domain.model.Manga;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MangaScraper {
    public static String BASE_URL = "https://mangalik.net/";
    
    // 🌟 المتغيرات السحرية التي ستحمل الكوكيز من الـ WebView 🌟
    public static String globalCookies = "";
    public static String globalUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";

    public interface ScrapingCallback {
        void onSuccess(List<Manga> mangas);
        void onError(String errorMessage);
    }

    public interface MangaDetailsCallback {
        void onSuccess(String description, String status, List<Chapter> chapters);
        void onError(String errorMessage);
    }

    public interface ChapterPagesCallback {
        void onSuccess(List<String> imageUrls);
        void onError(String errorMessage);
    }

    public static void fetchLatestManga(ScrapingCallback callback) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(BASE_URL)
                        .userAgent(globalUserAgent)
                        .header("Cookie", globalCookies)
                        .referrer(BASE_URL)
                        .timeout(15000)
                        .get();

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

                    Element imgElement = element.select(".item-thumb img, .post-title img").first();
                    if (imgElement == null) imgElement = element.select("img").first();

                    if (imgElement != null) {
                        String imgUrl = imgElement.hasAttr("data-src") ? imgElement.absUrl("data-src") : imgElement.absUrl("src");
                        if (imgElement.hasAttr("data-lazy-src")) imgUrl = imgElement.absUrl("data-lazy-src");
                        manga.setCoverUrl(imgUrl);
                    }

                    if (manga.getTitle() != null && !manga.getTitle().isEmpty() && manga.getUrl() != null) {
                        mangaList.add(manga);
                        uniqueUrls.add(manga.getUrl());
                    }
                }

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!mangaList.isEmpty()) callback.onSuccess(mangaList);
                    else callback.onError("لم يتم العثور على مانجات.");
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("خطأ في الاتصال: " + e.getMessage()));
            }
        }).start();
    }

    public static List<Manga> searchSingleSource(String sourceBaseUrl, String query) {
        List<Manga> mangaList = new ArrayList<>();
        try {
            String[] possibleUrls = {
                sourceBaseUrl + "?s=" + query.replace(" ", "+") + "&post_type=wp-manga",
                sourceBaseUrl + "?s=" + query.replace(" ", "+"),
                sourceBaseUrl + "search?q=" + query.replace(" ", "+"),
                sourceBaseUrl + "page/1/?s=" + query.replace(" ", "+") + "&post_type=wp-manga"
            };

            Document doc = null;
            for (String url : possibleUrls) {
                try {
                    doc = Jsoup.connect(url)
                            .userAgent(globalUserAgent)
                            .header("Cookie", globalCookies)
                            .referrer(sourceBaseUrl)
                            .timeout(10000)
                            .get();
                    break;
                } catch (Exception ignored) {}
            }

            if (doc != null) {
                Set<String> uniqueUrls = new HashSet<>();
                Elements mangaElements = doc.select(".c-tabs-item__content, .page-item-detail, .bsx, .listupd .bs");

                for (Element element : mangaElements) {
                    Manga manga = new Manga();
                    Element titleElement = element.select("h3 a, .post-title a, .tt, .bigor .tt").first();
                    if (titleElement != null) {
                        manga.setTitle(titleElement.text().trim());
                        manga.setUrl(titleElement.absUrl("href"));
                    } else {
                        Element link = element.select("a").first();
                        if (link != null) {
                            manga.setUrl(link.absUrl("href"));
                            manga.setTitle(link.attr("title"));
                        }
                    }

                    if (manga.getUrl() != null && uniqueUrls.contains(manga.getUrl())) continue;

                    Element imgElement = element.select(".tab-thumb img, .item-thumb img, img").first();
                    if (imgElement != null) {
                        String imgUrl = imgElement.hasAttr("data-src") ? imgElement.absUrl("data-src") : imgElement.absUrl("src");
                        if (imgElement.hasAttr("data-lazy-src")) imgUrl = imgElement.absUrl("data-lazy-src");
                        manga.setCoverUrl(imgUrl);
                    }

                    if (manga.getTitle() != null && !manga.getTitle().isEmpty() && manga.getUrl() != null) {
                        mangaList.add(manga);
                        uniqueUrls.add(manga.getUrl());
                    }
                }
            }
        } catch (Exception ignored) {}
        return mangaList;
    }

    public static void searchAllSources(String query, ScrapingCallback callback) {
        new Thread(() -> {
            String[] sources = SourceManager.getAllSources();

            List<Manga> combinedList = new ArrayList<>();
            Set<String> addedUrls = new HashSet<>();
            java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(sources.length);
            java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(sources.length);

            for (String src : sources) {
                executor.execute(() -> {
                    try {
                        List<Manga> res = searchSingleSource(src, query);
                        synchronized (combinedList) {
                            for (Manga m : res) {
                                if (m.getUrl() != null && !addedUrls.contains(m.getUrl())) {
                                    addedUrls.add(m.getUrl());
                                    combinedList.add(m);
                                }
                            }
                        }
                    } finally {
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
                else callback.onError("لم يتم العثور على نتائج في أي مصدر.");
            });
        }).start();
    }

            public static void searchAdvancedPaginated(String query, java.util.List<String> genres, String status, String type, int page, ScrapingCallback callback) {
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

    public static void searchMangaPaginated(String query, int page, ScrapingCallback callback) {
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

    public static void fetchMangaDetails(String mangaUrl, MangaDetailsCallback callback) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(mangaUrl)
                        .userAgent(globalUserAgent)
                        .header("Cookie", globalCookies)
                        .referrer(BASE_URL)
                        .timeout(15000)
                        .get();
                
                Element descElement = doc.select(".summary__content p, .manga-excerpt p, .description-summary p").first();
                String tempDescription = "لا يوجد وصف متاح.";
                if (descElement != null && !descElement.text().trim().isEmpty()) {
                    tempDescription = descElement.text().trim();
                } else {
                    Element fallbackDesc = doc.select(".summary__content, .manga-excerpt, .description-summary").first();
                    if (fallbackDesc != null) {
                        tempDescription = fallbackDesc.ownText().trim();
                    }
                }
                
                Element statusElement = doc.select(".post-status .summary-content, .post-status, .info-status").first();
                String tempStatus = statusElement != null ? statusElement.text().trim() : "مستمرة";

                List<Chapter> chapters = new ArrayList<>();
                Elements chapterElements = doc.select("li.wp-manga-chapter, .listing-chapters_wrap li, ul.main.version-chap li, .chapters-list li, .row-content-chapter li");
                
                if (chapterElements.isEmpty()) {
                    chapterElements = doc.select(".row-content-chapter a, .chapter-lieb a, .listing-chapters_wrap a");
                }

                for (Element el : chapterElements) {
                    Element link = el.tagName().equals("a") ? el : el.select("a").first();
                    if (link != null) {
                        String cUrl = link.absUrl("href");
                        String cTitle = link.text().trim();
                        if (!cUrl.isEmpty() && !cUrl.startsWith("javascript") && !cTitle.isEmpty()) {
                            Chapter chapter = new Chapter();
                            chapter.setUrl(cUrl);
                            chapter.setTitle(cTitle);
                            chapters.add(chapter);
                        }
                    }
                }
                
                final String finalDescription = tempDescription;
                final String finalStatus = tempStatus;

                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(finalDescription, finalStatus, chapters));
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("خطأ في الاتصال: " + e.getMessage()));
            }
        }).start();
    }

    public static String extractImageUrlFromImgTag(Element img) {
        if (img == null) return "";
        String[] attrs = {
            "data-src", "data-lazy-src", "data-cfsrc", "data-original", "data-full-url",
            "data-url", "data-lazy-url", "data-src-webp", "data-cdn-src", "data-lazy",
            "data-altsrc", "data-actualsrc", "data-pic", "data-img", "data-file",
            "data-echo", "data-image"
        };
        for (String attr : attrs) {
            if (img.hasAttr(attr)) {
                String val = img.attr(attr).trim();
                if (!val.isEmpty() && !val.startsWith("data:")) {
                    return fixUrl(val);
                }
            }
        }
        String srcset = img.hasAttr("data-srcset") ? img.attr("data-srcset") : img.attr("srcset");
        if (srcset != null && !srcset.trim().isEmpty() && !srcset.startsWith("data:")) {
            String first = srcset.split(",")[0].trim().split(" ")[0].trim();
            if (!first.isEmpty() && !first.startsWith("data:")) {
                return fixUrl(first);
            }
        }
        String src = img.attr("src").trim();
        if (!src.isEmpty() && !src.startsWith("data:")) {
            return fixUrl(src);
        }
        return "";
    }

    public static boolean isChapterPageImage(String rawUrl, Element imgElement) {
        if (rawUrl == null || rawUrl.trim().isEmpty() || rawUrl.startsWith("data:")) return false;
        String lower = rawUrl.toLowerCase(java.util.Locale.ROOT);
        String[] blacklist = {
            "logo", "banner", "header", "footer", "sidebar", "avatar", "favicon", "loader",
            "loading", "widget", "social", "discord", "telegram", "facebook", "twitter",
            "instagram", "app-store", "google-play", "ads", "advertisement", "promo",
            "button", "site-logo", "branding", "nav-", "badge", "donate", "patreon",
            "paypal", "emoji", "cursorleft", "cursorright", "32x32", "192x192", "180x180", "270x270"
        };
        for (String item : blacklist) {
            if (lower.contains(item)) return false;
        }
        if (imgElement != null) {
            Element parent = imgElement.parent();
            int depth = 0;
            while (parent != null && depth < 6) {
                String tag = parent.tagName().toLowerCase(java.util.Locale.ROOT);
                String cls = parent.className().toLowerCase(java.util.Locale.ROOT);
                String id = parent.id().toLowerCase(java.util.Locale.ROOT);
                if (tag.equals("footer") || tag.equals("header") || tag.equals("nav") || tag.equals("aside")) {
                    return false;
                }
                for (String b : new String[]{"header", "nav", "footer", "sidebar", "logo", "banner", "comments", "widget", "social", "menu"}) {
                    if (cls.contains(b) || id.contains(b)) return false;
                }
                parent = parent.parent();
                depth++;
            }
        }
        return true;
    }

    public static String getHighResImageUrl(String url) {
        if (url == null || url.trim().isEmpty()) return url;
        String clean = url.split("\\?")[0];
        String query = url.contains("?") ? "?" + url.substring(url.indexOf("?") + 1) : "";
        clean = clean.replaceAll("-\\d+x\\d+-[a-zA-Z]+(\\.[a-zA-Z0-9]+)$", "$1");
        clean = clean.replaceAll("-\\d+x\\d+(\\.[a-zA-Z0-9]+)$", "$1");
        return clean + query;
    }

    public static String fixUrl(String rawUrl) {
        if (rawUrl == null) return "";
        String s = rawUrl.trim();
        if (s.startsWith("//")) {
            s = "https:" + s;
        } else if (s.startsWith("/")) {
            s = BASE_URL.replaceAll("/$", "") + s;
        }
        if (s.contains("wp.com/mangalik.net")) {
            s = "https://mangalik.net" + s.substring(s.indexOf("mangalik.net") + "mangalik.net".length());
        } else if (s.contains("wp.com/")) {
            s = s.replaceAll("https?://[a-zA-Z0-9-]+\\.wp\\.com/", "https://");
        }
        return s;
    }

    public static void fetchChapterPages(String chapterUrl, ChapterPagesCallback callback) {
        new Thread(() -> {
            try {
                Document doc = Jsoup.connect(chapterUrl)
                        .userAgent(globalUserAgent)
                        .header("Cookie", globalCookies)
                        .referrer(BASE_URL)
                        .timeout(15000)
                        .get();
                
                List<String> imageUrls = new ArrayList<>();
                Set<String> uniqueUrls = new HashSet<>();

                String[] imgSelectors = {
                    "div.reading-content img", "div.page-break img", ".wp-manga-chapter-img",
                    "div.single-chapter img", ".reader-area img", "#readerarea img",
                    ".read-container img", ".chapter-content img", ".reading-content-wrap img",
                    "div.entry-content img", "div.entry-content p img", "div.text-center img",
                    "div.text-left img", "div[id*='chapter'] img", "div[class*='chapter'] img",
                    ".main-col img", "div.post-content img", ".chapter-image img"
                };
                
                for (String sel : imgSelectors) {
                    Elements images = doc.select(sel);
                    for (Element img : images) {
                        String url = extractImageUrlFromImgTag(img);
                        if (isChapterPageImage(url, img) && !uniqueUrls.contains(url)) {
                            imageUrls.add(getHighResImageUrl(url));
                            uniqueUrls.add(url);
                        }
                    }
                    if (imageUrls.size() >= 3) break;
                }

                // Generic <img> selector fallback
                if (imageUrls.isEmpty()) {
                    Elements allImages = doc.select("img");
                    for (Element img : allImages) {
                        String url = extractImageUrlFromImgTag(img);
                        if (isChapterPageImage(url, img) && !uniqueUrls.contains(url)) {
                            imageUrls.add(getHighResImageUrl(url));
                            uniqueUrls.add(url);
                        }
                    }
                }

                // 🌟 Script Regex Fallback (in case pages are rendered dynamically via JavaScript array)
                if (imageUrls.isEmpty()) {
                    Elements scripts = doc.select("script");
                    java.util.regex.Pattern regex = java.util.regex.Pattern.compile("https?://[^\\s\"'<>]+\\.(?:jpg|jpeg|png|webp|gif|avif)(?:\\?[^\\s\"'<>]*)?", java.util.regex.Pattern.CASE_INSENSITIVE);
                    for (Element script : scripts) {
                        String html = script.html().replace("\\/", "/");
                        java.util.regex.Matcher matcher = regex.matcher(html);
                        while (matcher.find()) {
                            String foundUrl = matcher.group();
                            if (isChapterPageImage(foundUrl, null) && !uniqueUrls.contains(foundUrl)) {
                                imageUrls.add(getHighResImageUrl(foundUrl));
                                uniqueUrls.add(foundUrl);
                            }
                        }
                    }
                }
                
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!imageUrls.isEmpty()) callback.onSuccess(imageUrls);
                    else callback.onError("لم يتم العثور على صفحات الفصل.");
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("خطأ في تحميل الفصل: " + e.getMessage()));
            }
        }).start();
    }
}
