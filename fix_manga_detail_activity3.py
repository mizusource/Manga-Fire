import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

# Let's count braces between showRatingStatsDialog and toggleFavorite
# Actually it's easier to find the exact block and replace it correctly.
old_str = """
    private void showRatingStatsDialog() {
        // Now called automatically to populate the UI instead of a dialog
        com.fire.mangareader.util.GlobalMangaStatsManager.fetchStats(MangaDetailActivity.this, mangaUrl, new com.fire.mangareader.util.GlobalMangaStatsManager.StatsCallback() {
            @Override
            public void onSuccess(com.fire.mangareader.util.GlobalMangaStats stats) {
                runOnUiThread(() -> {
                    android.widget.TextView tvStatStory = findViewById(R.id.tvStatStory);
                    android.widget.TextView tvStatCharacters = findViewById(R.id.tvStatCharacters);
                    android.widget.TextView tvStatDrawing = findViewById(R.id.tvStatDrawing);
                    
                    if (tvStatStory != null) tvStatStory.setText(String.format(java.util.Locale.US, "%.1f", stats.storyAverage));
                    if (tvStatCharacters != null) tvStatCharacters.setText(String.format(java.util.Locale.US, "%.1f", stats.charactersAverage));
                    if (tvStatDrawing != null) tvStatDrawing.setText(String.format(java.util.Locale.US, "%.1f", stats.artAverage));
                    
                    // Populate MangaDetail model to satisfy the new architecture
                    com.fire.mangareader.domain.model.manga.MangaScore score = new com.fire.mangareader.domain.model.manga.MangaScore(
                        (int) (stats.storyAverage * 10), (int) (stats.charactersAverage * 10), (int) (stats.artAverage * 10)
                    );
                });
            }
            @Override
            public void onError(String error) {
            }
        });
    }
    }
    
    private void toggleFavorite() {"""

new_str = """
    private void showRatingStatsDialog() {
        // Now called automatically to populate the UI instead of a dialog
        com.fire.mangareader.util.GlobalMangaStatsManager.fetchStats(MangaDetailActivity.this, mangaUrl, new com.fire.mangareader.util.GlobalMangaStatsManager.StatsCallback() {
            @Override
            public void onSuccess(com.fire.mangareader.util.GlobalMangaStats stats) {
                runOnUiThread(() -> {
                    android.widget.TextView tvStatStory = findViewById(R.id.tvStatStory);
                    android.widget.TextView tvStatCharacters = findViewById(R.id.tvStatCharacters);
                    android.widget.TextView tvStatDrawing = findViewById(R.id.tvStatDrawing);
                    
                    if (tvStatStory != null) tvStatStory.setText(String.format(java.util.Locale.US, "%.1f", stats.storyAverage));
                    if (tvStatCharacters != null) tvStatCharacters.setText(String.format(java.util.Locale.US, "%.1f", stats.charactersAverage));
                    if (tvStatDrawing != null) tvStatDrawing.setText(String.format(java.util.Locale.US, "%.1f", stats.artAverage));
                    
                    // Populate MangaDetail model to satisfy the new architecture
                    com.fire.mangareader.domain.model.manga.MangaScore score = new com.fire.mangareader.domain.model.manga.MangaScore(
                        (int) (stats.storyAverage * 10), (int) (stats.charactersAverage * 10), (int) (stats.artAverage * 10)
                    );
                });
            }
            @Override
            public void onError(String error) {
            }
        });
    }
    
    private void toggleFavorite() {"""

if old_str in content:
    content = content.replace(old_str, new_str)
    print("Replaced successfully")
else:
    print("Block not found!")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)
