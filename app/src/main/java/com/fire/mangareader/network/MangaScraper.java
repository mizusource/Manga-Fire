package com.fire.mangareader.network;

import android.os.Handler;
import android.os.Looper;
import com.fire.mangareader.model.Chapter;
import com.fire.mangareader.model.Manga;
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
                String[] imgSelectors = {
                    ".reading-content img",
                    ".page-break img",
                    ".wp-manga-chapter-img",
                    ".text-center img",
                    "#readerarea img"
                };
                
                for (String sel : imgSelectors) {
                    Elements images = doc.select(sel);
                    for (Element img : images) {
                        String url = img.hasAttr("data-src") ? img.absUrl("data-src") 
                                   : img.hasAttr("data-lazy-src") ? img.absUrl("data-lazy-src")
                                   : img.absUrl("src");
                        
                        if (url != null && !url.trim().isEmpty() && !url.contains("data:image")) {
                            imageUrls.add(url.trim());
                        }
                    }
                    if (!imageUrls.isEmpty()) break;
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
