import re

filepath = 'app/src/main/java/com/fire/mangareader/data/network/ApiMangaScraper.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import com.fire.mangareader.util.MangaScraper;', 'import com.fire.mangareader.data.network.MangaScraper;')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched ApiMangaScraper.java")
