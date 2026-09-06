import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

target = """                    if (finalStatus.equals("favorite")) {
                        item.setFavorite(true);
                        // don't overwrite reading status if just favoriting
                    } else {
                        item.setStatus(finalStatus);
                    }
                    AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().insert(item);
                    
                    runOnUiThread(() -> {
                        currentLibraryStatus = finalStatus;
                        updateFavoriteIcon();"""

replacement = """                    if (finalStatus.equals("favorite")) {
                        item.setFavorite(true);
                        // don't overwrite reading status if just favoriting
                        com.fire.mangareader.data.local.DatabaseBridge.toggleFavorite(MangaDetailActivity.this, mangaUrl, mangaTitle, mangaCover, true);
                    } else {
                        item.setStatus(finalStatus);
                        if (finalStatus.equals("reading") || finalStatus.equals("completed") || finalStatus.equals("plan_to_read")) {
                            com.fire.mangareader.data.local.DatabaseBridge.toggleFavorite(MangaDetailActivity.this, mangaUrl, mangaTitle, mangaCover, true);
                        } else {
                            com.fire.mangareader.data.local.DatabaseBridge.toggleFavorite(MangaDetailActivity.this, mangaUrl, mangaTitle, mangaCover, false);
                        }
                    }
                    AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().insert(item);
                    
                    runOnUiThread(() -> {
                        currentLibraryStatus = finalStatus;
                        updateFavoriteIcon();"""

content = content.replace(target, replacement)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

