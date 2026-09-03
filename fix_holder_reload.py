import re

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonPageHolder.java", "r") as f:
    content = f.read()

# It currently has:
# private String currentUrl;
# Let's add: private com.fire.mangareader.domain.model.reader.Page currentPage;
content = content.replace("private String currentUrl;", "private String currentUrl;\n    private com.fire.mangareader.domain.model.reader.Page currentPage;")

# Replace the bind line
content = content.replace("this.currentUrl = imageUrl;", "this.currentUrl = imageUrl;\n        this.currentPage = page;")

# Replace the errorLayout reload listener
content = content.replace("bind(currentUrl, currentCookies, refererUrl);", "if (currentPage != null) { bind(currentPage, currentCookies, refererUrl); }")

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonPageHolder.java", "w") as f:
    f.write(content)
