import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

# Make sure HeroBannerAdapter is imported
if 'import com.fire.mangareader.adapter.HeroBannerAdapter;' not in content:
    content = content.replace('import com.fire.mangareader.adapter.MangaAdapter;', 'import com.fire.mangareader.adapter.MangaAdapter;\nimport com.fire.mangareader.adapter.HeroBannerAdapter;')

# Find where we set the adapter for rvLatestUpdates
adapter_setup_target = """                    if (!fetchedList.isEmpty()) {
                        mangaList.clear();
                        mangaList.addAll(fetchedList);"""

adapter_setup_replacement = """                    if (!fetchedList.isEmpty()) {
                        mangaList.clear();
                        mangaList.addAll(fetchedList);
                        
                        // Setup Hero Banner with top 3-5 mangas
                        if (mangaList.size() > 3) {
                            java.util.List<Manga> bannerList = new java.util.ArrayList<>(mangaList.subList(0, Math.min(5, mangaList.size())));
                            HeroBannerAdapter bannerAdapter = new HeroBannerAdapter(MainActivity.this, bannerList);
                            vpHeroBanner.setAdapter(bannerAdapter);
                            
                            // Remove them from main list to avoid duplication if preferred, or keep them.
                            // Let's keep them so the list is full.
                        }
"""

content = content.replace(adapter_setup_target, adapter_setup_replacement)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
