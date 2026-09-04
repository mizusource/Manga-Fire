import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r", encoding="utf-8") as f:
    content = f.read()

# 1. Patch fetchLatestManga
pattern1 = r'(public static void fetchLatestManga\(ScrapingCallback callback\) \{)'
replacement1 = r'\1\n        if (BASE_URL.contains("dilar.tube")) {\n            DilarScraper.fetchLatestManga(callback);\n            return;\n        }\n'
content = re.sub(pattern1, replacement1, content)

# 2. Patch fetchMangaDetails
pattern2 = r'(public static void fetchMangaDetails\(String mangaUrl, MangaDetailsCallback callback\) \{)'
replacement2 = r'\1\n        if (mangaUrl.contains("dilar.tube")) {\n            DilarScraper.fetchMangaDetails(mangaUrl, callback);\n            return;\n        }\n'
content = re.sub(pattern2, replacement2, content)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w", encoding="utf-8") as f:
    f.write(content)
print("patched scraper")
