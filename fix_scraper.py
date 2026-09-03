import re

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "r") as f:
    content = f.read()

# Replace patterns like:
# if (!combinedList.isEmpty()) callback.onSuccess(combinedList);
# else callback.onError("لم يتم العثور على أي نتائج إضافية.");
# With just:
# callback.onSuccess(combinedList);

content = re.sub(
    r'if\s*\(!combinedList\.isEmpty\(\)\)\s*callback\.onSuccess\(combinedList\);\s*else\s*callback\.onError\([^)]+\);',
    'callback.onSuccess(combinedList);',
    content
)

content = re.sub(
    r'if\s*\(!mangaList\.isEmpty\(\)\)\s*callback\.onSuccess\(mangaList\);\s*else\s*callback\.onError\([^)]+\);',
    'callback.onSuccess(mangaList);',
    content
)

with open("app/src/main/java/com/fire/mangareader/data/network/MangaScraper.java", "w") as f:
    f.write(content)
