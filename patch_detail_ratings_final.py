import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Fix duplicates in loadAniListMetadata
old_dup = '''                    TextView tvALRating = findViewById(R.id.tvALRating);
                    TextView tvALRatingCount = findViewById(R.id.tvALRatingCount);
                    if (tvALRating != null && metadata.averageScore > 0) {'''
new_dup = '''                    if (tvALRating != null && metadata.averageScore > 0) {'''
content = content.replace(old_dup, new_dup)

# Fix tvRatingScore in onSuccess (RatingCallback)
old_cb = '''                    runOnUiThread(() -> {
                        TextView tvALRating = findViewById(R.id.tvALRating);
                        TextView tvALRatingCount = findViewById(R.id.tvALRatingCount);
                        if (tvRatingScore != null) tvRatingScore.setText(String.format(java.util.Locale.US, "%.1f/10", newAverage));
                        if (tvRatingCount != null) tvRatingCount.setText(totalVotes + " صوت");
                        Toast.makeText(MangaDetailActivity.this, "تم تسجيل تقييمك بنجاح! شكرًا لك ⭐", Toast.LENGTH_SHORT).show();
                    });'''
new_cb = '''                    runOnUiThread(() -> {
                        TextView tvGlobalRating = findViewById(R.id.tvGlobalRating);
                        TextView tvGlobalRatingCount = findViewById(R.id.tvGlobalRatingCount);
                        if (tvGlobalRating != null) tvGlobalRating.setText(String.format(java.util.Locale.US, "%.1f/10", newAverage));
                        if (tvGlobalRatingCount != null) tvGlobalRatingCount.setText(String.valueOf(totalVotes));
                        Toast.makeText(MangaDetailActivity.this, "تم تسجيل تقييمك بنجاح! شكرًا لك ⭐", Toast.LENGTH_SHORT).show();
                    });'''
content = content.replace(old_cb, new_cb)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
