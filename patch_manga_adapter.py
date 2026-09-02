import os

filepath = 'app/src/main/java/com/fire/mangareader/presentation/adapter/MangaAdapter.java'
with open(filepath, 'r') as f:
    content = f.read()

old_glide = """        // تحميل صورة الغلاف عبر مكتبة Glide
        Glide.with(context)
             .load(manga.getCoverUrl())
             .override(300, 400)
             .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
             .into(holder.imgCover);"""

new_glide = """        // تحميل صورة الغلاف مع تأثير انتقال ناعم
        com.fire.mangareader.util.ImageExtensions.loadWithCrossFade(holder.imgCover, manga.getCoverUrl());"""

if old_glide in content:
    content = content.replace(old_glide, new_glide)
    with open(filepath, 'w') as f:
        f.write(content)
    print("MangaAdapter patched.")
else:
    print("Could not find Glide code in MangaAdapter.")
