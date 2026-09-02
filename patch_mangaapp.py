import re

filepath = 'app/src/main/java/com/fire/mangareader/MangaApp.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('super.onCreate();', 'super.onCreate();\n        com.fire.mangareader.util.MangaOkHttp.init(this);')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MangaApp.java")
