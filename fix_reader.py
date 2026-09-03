import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "r") as f:
    content = f.read()

# Replace the inner fallbackSelectors and fallbackTransforms declarations with just assignments
content = content.replace("java.util.Map<String, com.fire.mangareader.domain.model.parser.SelectorConfig> fallbackSelectors = new java.util.HashMap<>();", "")
content = content.replace("java.util.List<com.fire.mangareader.domain.model.parser.UrlTransformation> fallbackTransforms = new java.util.ArrayList<>();", "")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/ChapterReaderActivity.java", "w") as f:
    f.write(content)
print("Fixed ChapterReaderActivity")
