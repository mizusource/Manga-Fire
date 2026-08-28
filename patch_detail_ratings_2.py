import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Fix lines 571
old_rating_update = '''                    if (tvRatingScore != null && metadata.averageScore > 0) {
                        double scoreOutOf10 = metadata.averageScore / 10.0;
                        tvRatingScore.setText(String.format(java.util.Locale.US, "%.1f/10", scoreOutOf10));
                    }
                    if (tvRatingCount != null && metadata.popularity > 0) {
                        tvRatingCount.setText(metadata.popularity + " متابع 🌟");
                    }'''
new_rating_update = '''                    TextView tvALRating = findViewById(R.id.tvALRating);
                    TextView tvALRatingCount = findViewById(R.id.tvALRatingCount);
                    if (tvALRating != null && metadata.averageScore > 0) {
                        double scoreOutOf10 = metadata.averageScore / 10.0;
                        tvALRating.setText(String.format(java.util.Locale.US, "%.1f/10", scoreOutOf10));
                    }
                    if (tvALRatingCount != null && metadata.popularity > 0) {
                        int pop = metadata.popularity;
                        tvALRatingCount.setText(pop >= 1000 ? (pop / 1000) + "K" : String.valueOf(pop));
                    }'''

content = content.replace(old_rating_update, new_rating_update)

old_rating_649 = '''                        TextView tvRatingScore = findViewById(R.id.tvRatingScore);
                        TextView tvRatingCount = findViewById(R.id.tvRatingCount);'''
new_rating_649 = '''                        TextView tvALRating = findViewById(R.id.tvALRating);
                        TextView tvALRatingCount = findViewById(R.id.tvALRatingCount);'''

content = content.replace(old_rating_649, new_rating_649)


old_rating_null_set = '''                        if (tvRatingScore != null) tvRatingScore.setText("N/A");
                        if (tvRatingCount != null) tvRatingCount.setText("0");'''
new_rating_null_set = '''                        if (tvALRating != null) tvALRating.setText("N/A");
                        if (tvALRatingCount != null) tvALRatingCount.setText("0");'''

content = content.replace(old_rating_null_set, new_rating_null_set)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

