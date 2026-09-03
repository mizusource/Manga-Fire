import re

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonPageHolder.java", "r") as f:
    content = f.read()

old_bind = "public void bind(String imageUrl, String cookies, String referer) {"
new_bind = "public void bind(final com.fire.mangareader.domain.model.reader.Page page, String cookies, String referer) {\n        String imageUrl = page.getImageUrl() != null ? page.getImageUrl() : page.getUrl();"

if old_bind in content:
    content = content.replace(old_bind, new_bind)
else:
    print("Could not find bind method!")

with open("app/src/main/java/com/fire/mangareader/presentation/reader/viewer/WebtoonPageHolder.java", "w") as f:
    f.write(content)
