import os

filepath = 'app/src/main/java/com/fire/mangareader/utils/AniListManager.java'
with open(filepath, 'r') as f:
    content = f.read()

old_assignments = """                        meta.format = media.optString("format", "MANGA");
                        meta.sourceFormat = meta.format;
                        meta.countryOfOrigin = media.optString("countryOfOrigin", "JP");
                        meta.originCountry = meta.countryOfOrigin;"""

new_assignments = """                        meta.sourceFormat = media.optString("format", "MANGA");
                        meta.format = MangaExtensions.getMangaFormat(meta.sourceFormat);
                        meta.originCountry = media.optString("countryOfOrigin", "JP");
                        meta.countryOfOrigin = MangaExtensions.getMangaType(meta.originCountry);"""

content = content.replace(old_assignments, new_assignments)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched AniListManager.java again")
