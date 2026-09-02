import re

filepath = 'app/src/main/java/com/fire/mangareader/data/network/MangaGlideModule.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('new OkHttpUrlLoader.Factory(client)', 'new OkHttpUrlLoader.Factory(com.fire.mangareader.util.MangaOkHttp.getClient())')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MangaGlideModule")
