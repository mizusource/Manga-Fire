import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

recs_block = """
        androidx.recyclerview.widget.RecyclerView recommendationsRecycler = findViewById(R.id.recommendationsRecyclerView);
        if (recommendationsRecycler != null) {
            recommendationsRecycler.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            // Just displaying an empty list for now as an architectural skeleton for recommendations
            com.fire.mangareader.presentation.adapter.MangaAdapter recsAdapter = new com.fire.mangareader.presentation.adapter.MangaAdapter(new java.util.ArrayList<>(), manga -> {
                android.content.Intent intent = new android.content.Intent(this, MangaDetailActivity.class);
                intent.putExtra("mangaUrl", manga.getUrl());
                intent.putExtra("mangaTitle", manga.getTitle());
                intent.putExtra("mangaCover", manga.getCoverUrl());
                startActivity(intent);
            });
            recommendationsRecycler.setAdapter(recsAdapter);
        }
"""

if "recommendationsRecyclerView" not in content:
    content = content.replace("swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);", 
    "swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);\n" + recs_block)

    with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
        f.write(content)
    print("Patched Recs")
else:
    print("Already there")
