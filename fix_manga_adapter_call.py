import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

bad_call = """            com.fire.mangareader.presentation.adapter.MangaAdapter recsAdapter = new com.fire.mangareader.presentation.adapter.MangaAdapter(new java.util.ArrayList<>(), manga -> {
                android.content.Intent intent = new android.content.Intent(this, MangaDetailActivity.class);
                intent.putExtra("mangaUrl", manga.getUrl());
                intent.putExtra("mangaTitle", manga.getTitle());
                intent.putExtra("mangaCover", manga.getCoverUrl());
                startActivity(intent);
            });"""

good_call = """            com.fire.mangareader.presentation.adapter.MangaAdapter recsAdapter = new com.fire.mangareader.presentation.adapter.MangaAdapter(this, new java.util.ArrayList<>());"""

content = content.replace(bad_call, good_call)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)
