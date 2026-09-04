import re

with open("app/src/main/java/com/fire/mangareader/data/network/SmartOkHttpUrlLoader.java", "r") as f:
    text = f.read()

pattern1 = r'private final OkHttpUrlLoader specialLoader;'
replacement1 = r'private final ModelLoader<GlideUrl, InputStream> specialLoader;'
text = re.sub(pattern1, replacement1, text)

pattern2 = r'private final OkHttpUrlLoader defaultLoader;'
replacement2 = r'private final ModelLoader<GlideUrl, InputStream> defaultLoader;'
text = re.sub(pattern2, replacement2, text)

pattern3 = r'public SmartOkHttpUrlLoader\(OkHttpUrlLoader specialLoader, OkHttpUrlLoader defaultLoader\) \{'
replacement3 = r'public SmartOkHttpUrlLoader(ModelLoader<GlideUrl, InputStream> specialLoader, ModelLoader<GlideUrl, InputStream> defaultLoader) {'
text = re.sub(pattern3, replacement3, text)

with open("app/src/main/java/com/fire/mangareader/data/network/SmartOkHttpUrlLoader.java", "w") as f:
    f.write(text)
