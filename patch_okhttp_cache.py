import re

with open("app/src/main/java/com/fire/mangareader/util/MangaOkHttp.java", "r") as f:
    content = f.read()

# Make sure to import okhttp3.Cache
if "import okhttp3.Cache;" not in content:
    content = content.replace("import okhttp3.ConnectionPool;", "import okhttp3.ConnectionPool;\nimport okhttp3.Cache;\nimport java.io.File;")

# Add Cache configuration
cache_logic = """
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .cache(new Cache(new File(appContext.getCacheDir(), "manga_cache"), 20L * 1024L * 1024L))
"""
content = content.replace(".writeTimeout(30, TimeUnit.SECONDS)", cache_logic.strip())

with open("app/src/main/java/com/fire/mangareader/util/MangaOkHttp.java", "w") as f:
    f.write(content)

