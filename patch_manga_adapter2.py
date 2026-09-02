import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/adapter/MangaAdapter.java'
with open(filepath, 'r') as f:
    content = f.read()

pattern = r'Glide\.with\(context\).*?\.into\(holder\.imgCover\);'
new_glide = 'com.fire.mangareader.util.ImageExtensions.loadWithCrossFade(holder.imgCover, manga.getCoverUrl());'

content = re.sub(pattern, new_glide, content, flags=re.DOTALL)

with open(filepath, 'w') as f:
    f.write(content)
print("MangaAdapter patched.")
