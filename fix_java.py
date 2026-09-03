import re

# Fix MangaApp.java
with open("app/src/main/java/com/fire/mangareader/MangaApp.java", "r") as f:
    content = f.read()

content = content.replace("        com.fire.mangareader.util.        com.fire.mangareader.util.MangaOkHttp.init(this);", 
                          "        com.fire.mangareader.util.MangaOkHttp.init(this);")

with open("app/src/main/java/com/fire/mangareader/MangaApp.java", "w") as f:
    f.write(content)

# Fix ChapterReaderActivity.java
with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

bad_selectors = "java.util.Map<String, com.fire.mangareader.domain.model.parser.SelectorConfig> selectors = new java.util.HashMap<>();"
bad_transforms = "java.util.List<com.fire.mangareader.domain.model.parser.UrlTransformation> transforms = new java.util.ArrayList<>();"

# I'll just change the inner block to use existing variables or rename them.
# The code I inserted was:
# java.util.Map<String, com.fire.mangareader.domain.model.parser.SelectorConfig> selectors = new java.util.HashMap<>();
# selectors.put("url", ...
# Let's rename them inside the `if (config == null)` block to fallbackSelectors and fallbackTransforms.
content = content.replace(bad_selectors, "java.util.Map<String, com.fire.mangareader.domain.model.parser.SelectorConfig> fallbackSelectors = new java.util.HashMap<>();")
content = content.replace("selectors.put(\"url\",", "fallbackSelectors.put(\"url\",")
content = content.replace(bad_transforms, "java.util.List<com.fire.mangareader.domain.model.parser.UrlTransformation> fallbackTransforms = new java.util.ArrayList<>();")
content = content.replace("transforms.add(", "fallbackTransforms.add(")
content = content.replace("selectors,\n                            com.fire.mangareader.domain.model.parser.ResponseFormat.HTML,\n                            transforms", "fallbackSelectors,\n                            com.fire.mangareader.domain.model.parser.ResponseFormat.HTML,\n                            fallbackTransforms")


with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(content)

print("Fixed variables and typos.")
