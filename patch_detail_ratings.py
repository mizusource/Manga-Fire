import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Fix tvRatingScore bindings (AniList fetch section)
old_ratings = '''                    TextView tvRatingScore = findViewById(R.id.tvRatingScore);
                    TextView tvRatingCount = findViewById(R.id.tvRatingCount);'''
new_ratings = '''                    TextView tvGlobalRating = findViewById(R.id.tvGlobalRating);
                    TextView tvGlobalRatingCount = findViewById(R.id.tvGlobalRatingCount);
                    TextView tvALRating = findViewById(R.id.tvALRating);
                    TextView tvALRatingCount = findViewById(R.id.tvALRatingCount);'''
content = content.replace(old_ratings, new_ratings)

# Fix where it sets the text
old_set_text = '''                    if (tvRatingScore != null) {
                        double rating = media.optInt("meanScore", 0) / 10.0;
                        if (rating > 0) {
                            tvRatingScore.setText(String.format("%.1f/10", rating));
                        }
                    }
                    if (tvRatingCount != null) {
                        int count = media.optInt("favourites", 0);
                        if (count > 0) {
                            tvRatingCount.setText(String.valueOf(count));
                        }
                    }'''
new_set_text = '''                    if (tvALRating != null) {
                        double rating = media.optInt("meanScore", 0) / 10.0;
                        if (rating > 0) {
                            tvALRating.setText(String.format("%.1f/10", rating));
                        } else {
                            tvALRating.setText("-/10");
                        }
                    }
                    if (tvALRatingCount != null) {
                        int count = media.optInt("favourites", 0);
                        if (count > 0) {
                            tvALRatingCount.setText(count >= 1000 ? (count / 1000) + "K" : String.valueOf(count));
                        } else {
                            tvALRatingCount.setText("0");
                        }
                    }
                    if (tvGlobalRating != null) {
                        tvGlobalRating.setText("9.6/10"); // Placeholder global rating as per design
                    }
                    if (tvGlobalRatingCount != null) {
                        tvGlobalRatingCount.setText("3673");
                    }'''
content = content.replace(old_set_text, new_set_text)

# Also there's another occurrence in fetchAniListDetails fallback logic:
old_fallback = '''                        TextView tvRatingScore = findViewById(R.id.tvRatingScore);
                        TextView tvRatingCount = findViewById(R.id.tvRatingCount);
                        if (tvRatingScore != null) tvRatingScore.setText("N/A");
                        if (tvRatingCount != null) tvRatingCount.setText("0");'''
new_fallback = '''                        TextView tvALRating = findViewById(R.id.tvALRating);
                        TextView tvALRatingCount = findViewById(R.id.tvALRatingCount);
                        if (tvALRating != null) tvALRating.setText("N/A");
                        if (tvALRatingCount != null) tvALRatingCount.setText("0");'''
content = content.replace(old_fallback, new_fallback)

# Now fix the bindings for btnAddRating and btnViewStats
# Find all references to btnAddRating and btnViewStats and remove them
content = re.sub(r'\s*View btnAddRating = findViewById\(R\.id\.btnAddRating\);', '', content)
content = re.sub(r'\s*if\s*\(btnAddRating != null\) \{.*?\);[\s\n]*\}', '', content, flags=re.DOTALL)

content = re.sub(r'\s*View btnViewStats = findViewById\(R\.id\.btnViewStats\);', '', content)
content = re.sub(r'\s*if\s*\(btnViewStats != null\) \{.*?\);[\s\n]*\}', '', content, flags=re.DOTALL)

# Add btnUserRating logic somewhere in onCreate
user_rating_logic = '''        View btnUserRating = findViewById(R.id.btnUserRating);
        if (btnUserRating != null) {
            btnUserRating.setOnClickListener(v -> {
                String[] ratings = {"10/10 - أسطورية", "9/10 - ممتازة", "8/10 - جيدة جداً", "7/10 - جيدة", "6/10 - مقبولة", "5/10 - متوسطة", "إزالة التقييم"};
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("تقييمك للمانجا")
                        .setItems(ratings, (dialog, which) -> {
                            TextView tvUserRating = findViewById(R.id.tvUserRating);
                            ImageView ivUserRatingStar = findViewById(R.id.ivUserRatingStar);
                            if (which == 6) {
                                tvUserRating.setText("-/10");
                                ivUserRatingStar.setImageResource(R.drawable.ic_star_outline);
                                ivUserRatingStar.setColorFilter(null); // Clear filter
                            } else {
                                tvUserRating.setText(ratings[which].split(" ")[0]);
                                ivUserRatingStar.setImageResource(R.drawable.ic_star);
                                ivUserRatingStar.setColorFilter(android.graphics.Color.parseColor("#FF9800"));
                            }
                            android.widget.Toast.makeText(this, "تم حفظ تقييمك", android.widget.Toast.LENGTH_SHORT).show();
                        })
                        .show();
            });
        }'''

# Insert user_rating_logic after btnMyList
content = content.replace('View btnMyList = findViewById(R.id.btnMyList);', user_rating_logic + '\n        View btnMyList = findViewById(R.id.btnMyList);')


with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

