import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

# Add getDocument helper
helper = """
    public static Document getDocument(String url) throws Exception {
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();
        okhttp3.Response response = com.fire.mangareader.util.MangaOkHttp.getClient().newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new Exception("HTTP " + response.code());
        }
        String html = response.body().string();
        return Jsoup.parse(html, url);
    }
"""

if "public static Document getDocument" not in content:
    content = content.replace("public interface ChapterPagesCallback {", helper + "\n    public interface ChapterPagesCallback {")

# Replace Jsoup.connect
pattern = r'Jsoup\.connect\([^)]+\)\s*\.userAgent\([^)]+\)\s*\.header\([^)]+\)\s*\.referrer\([^)]+\)\s*\.timeout\([^)]+\)\s*\.get\(\)'

def replacer(match):
    # Extract the URL parameter from Jsoup.connect(...)
    s = match.group(0)
    m = re.search(r'Jsoup\.connect\(([^)]+)\)', s)
    if m:
        url = m.group(1)
        return f"getDocument({url})"
    return s

content = re.sub(pattern, replacer, content)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)
