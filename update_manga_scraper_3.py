import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

new_details_logic = """
                List<Chapter> chapters = new ArrayList<>();
                Elements chapterElements = doc.select("li.wp-manga-chapter, .listing-chapters_wrap li, ul.main.version-chap li, .chapters-list li, .row-content-chapter li");
                
                if (chapterElements.isEmpty()) {
                    chapterElements = doc.select(".row-content-chapter a, .chapter-lieb a, .listing-chapters_wrap a");
                }

                // If chapters are empty, try AJAX fetching (Madara theme)
                if (chapterElements.isEmpty() && mangaUrl.contains("mangalik.net")) {
                    String mangaId = "";
                    Element idElement = doc.select("#manga-chapters-holder").first();
                    if (idElement != null && idElement.hasAttr("data-id")) {
                        mangaId = idElement.attr("data-id");
                    } else {
                        Element ratingElementId = doc.select(".rating-post-id").first();
                        if (ratingElementId != null) {
                            mangaId = ratingElementId.val();
                        } else {
                            Element bookmarkElement = doc.select(".wp-manga-action-button[data-action=bookmark]").first();
                            if (bookmarkElement != null) {
                                mangaId = bookmarkElement.attr("data-post");
                            } else {
                                Element anyPost = doc.select("[data-post-id]").first();
                                if (anyPost != null) {
                                    mangaId = anyPost.attr("data-post-id");
                                }
                            }
                        }
                    }

                    if (!mangaId.isEmpty()) {
                        try {
                            okhttp3.OkHttpClient client = com.fire.mangareader.util.MangaOkHttp.getClient();
                            String ajaxUrl = "https://mangalik.net/wp-admin/admin-ajax.php";
                            okhttp3.RequestBody requestBody = new okhttp3.FormBody.Builder()
                                    .add("action", "manga_get_chapters")
                                    .add("manga", mangaId)
                                    .build();
                            okhttp3.Request request = new okhttp3.Request.Builder()
                                    .url(ajaxUrl)
                                    .post(requestBody)
                                    .addHeader("X-Requested-With", "XMLHttpRequest")
                                    .addHeader("User-Agent", globalUserAgent)
                                    .addHeader("Cookie", globalCookies)
                                    .build();
                            okhttp3.Response response = client.newCall(request).execute();
                            if (response.isSuccessful() && response.body() != null) {
                                String ajaxHtml = response.body().string();
                                Document ajaxDoc = Jsoup.parse(ajaxHtml, mangaUrl);
                                chapterElements = ajaxDoc.select("li.wp-manga-chapter, .listing-chapters_wrap li, ul.main.version-chap li, .chapters-list li, .row-content-chapter li");
                                if (chapterElements.isEmpty()) {
                                    chapterElements = ajaxDoc.select(".row-content-chapter a, .chapter-lieb a, .listing-chapters_wrap a");
                                }
                            }
                        } catch (Exception ignored) { }
                    }
                }

                for (Element el : chapterElements) {
"""

pattern = r"List<Chapter> chapters = new ArrayList\<\>\(\);\s*Elements chapterElements = doc\.select\([^;]+;\s*if \(chapterElements\.isEmpty\(\)\) \{\s*chapterElements = doc\.select\([^;]+;\s*\}\s*for \(Element el : chapterElements\) \{"

content = re.sub(pattern, new_details_logic.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)

