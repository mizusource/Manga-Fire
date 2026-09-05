import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

# Replace the first Jsoup.connect
new_ajax_1 = """
                        okhttp3.OkHttpClient client = com.fire.mangareader.util.MangaOkHttp.getClient();
                        okhttp3.RequestBody reqBody = okhttp3.RequestBody.create(null, new byte[0]);
                        okhttp3.Request request = new okhttp3.Request.Builder()
                            .url(mangaUrl + (mangaUrl.endsWith("/") ? "" : "/") + "ajax/chapters/")
                            .post(reqBody)
                            .addHeader("User-Agent", com.fire.mangareader.data.network.MangaScraper.globalUserAgent)
                            .addHeader("Cookie", com.fire.mangareader.data.network.MangaScraper.globalCookies)
                            .build();
                        okhttp3.Response response = client.newCall(request).execute();
                        if (response.isSuccessful() && response.body() != null) {
                            org.jsoup.nodes.Document ajaxDoc = org.jsoup.Jsoup.parse(response.body().string(), mangaUrl);
                            chapterElements = ajaxDoc.select("li.wp-manga-chapter, .listing-chapters_wrap li, .row-content-chapter li, a[href*='chapter']");
                        }
"""

pattern1 = r"org\.jsoup\.nodes\.Document ajaxDoc = org\.jsoup\.Jsoup\.connect\(mangaUrl \+ \(mangaUrl\.endsWith\(\"\/\"\) \? \"\" : \"\/\"\) \+ \"ajax\/chapters\/\"\).*?\.post\(\);\s*chapterElements = ajaxDoc\.select\(\"li\.wp-manga-chapter, \.listing-chapters_wrap li, \.row-content-chapter li, a\[href\*='chapter'\]\"\);"
content = re.sub(pattern1, new_ajax_1.strip(), content, flags=re.DOTALL)

# Replace the second Jsoup.connect
new_ajax_2 = """
                            okhttp3.OkHttpClient client = com.fire.mangareader.util.MangaOkHttp.getClient();
                            okhttp3.RequestBody requestBody = new okhttp3.FormBody.Builder()
                                    .add("action", "manga_get_chapters")
                                    .add("manga", mangaId)
                                    .build();
                            okhttp3.Request request = new okhttp3.Request.Builder()
                                    .url(ajaxUrl)
                                    .post(requestBody)
                                    .addHeader("X-Requested-With", "XMLHttpRequest")
                                    .addHeader("User-Agent", com.fire.mangareader.data.network.MangaScraper.globalUserAgent)
                                    .addHeader("Cookie", com.fire.mangareader.data.network.MangaScraper.globalCookies)
                                    .build();
                            okhttp3.Response response = client.newCall(request).execute();
                            if (response.isSuccessful() && response.body() != null) {
                                org.jsoup.nodes.Document ajaxDoc = org.jsoup.Jsoup.parse(response.body().string(), ajaxUrl);
                                chapterElements = ajaxDoc.select("li.wp-manga-chapter, .listing-chapters_wrap li, .row-content-chapter li, a[href*='chapter']");
                            }
"""

pattern2 = r"org\.jsoup\.nodes\.Document ajaxDoc = org\.jsoup\.Jsoup\.connect\(ajaxUrl\).*?\.post\(\);\s*chapterElements = ajaxDoc\.select\(\"li\.wp-manga-chapter, \.listing-chapters_wrap li, \.row-content-chapter li, a\[href\*='chapter'\]\"\);"
content = re.sub(pattern2, new_ajax_2.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

