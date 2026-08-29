import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

content = content.replace(
    'com.fire.mangareader.utils.GlobalMangaStatsManager.submitRating(mangaUrl, mangaTitle, overall, story, characters, art, new',
    'com.fire.mangareader.utils.GlobalMangaStatsManager.submitRating(MangaDetailActivity.this, mangaUrl, mangaTitle, overall, story, characters, art, new'
)

content = content.replace(
    'com.fire.mangareader.utils.GlobalMangaStatsManager.fetchStats(mangaUrl, new',
    'com.fire.mangareader.utils.GlobalMangaStatsManager.fetchStats(MangaDetailActivity.this, mangaUrl, new'
)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

