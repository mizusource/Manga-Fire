import re
import os

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Replace tvAniListCountry with chipAniListCountry
content = content.replace('R.id.tvAniListCountry', 'R.id.chipAniListCountry')
content = content.replace('TextView tvAniListCountry = findViewById', 'android.widget.TextView tvAniListCountry = findViewById')

# Add logic to dynamically populate genres
# We need to find where AniList response is parsed
# Around: if (tvAniListCountry != null && metadata.countryOfOrigin != null) {

genre_logic = """
                    if (tvAniListCountry != null && metadata.countryOfOrigin != null) {
                        tvAniListCountry.setText(metadata.countryOfOrigin);
                    }
                    
                    android.widget.TextView chipFormat = findViewById(R.id.chipAniListFormat);
                    if (chipFormat != null && metadata.format != null) {
                        chipFormat.setText(metadata.format);
                    }

                    com.google.android.material.chip.ChipGroup chipGroup = findViewById(R.id.chipGroupGenres);
                    if (chipGroup != null && metadata.genres != null && !metadata.genres.isEmpty()) {
                        for (String genre : metadata.genres) {
                            com.google.android.material.chip.Chip chip = new com.google.android.material.chip.Chip(MangaDetailActivity.this);
                            chip.setText(genre);
                            chip.setTextColor(android.graphics.Color.WHITE);
                            chip.setChipBackgroundColorResource(android.R.color.transparent);
                            chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#33FFFFFF")));
                            chip.setChipStrokeWidth(0f);
                            chipGroup.addView(chip);
                        }
                    }
"""

# Find the block:
# if (tvAniListCountry != null && metadata.countryOfOrigin != null) {
#     tvAniListCountry.setText("دولة المنشأ: " + metadata.countryOfOrigin);
# }

old_country_logic = """                    if (tvAniListCountry != null && metadata.countryOfOrigin != null) {
                        tvAniListCountry.setText("دولة المنشأ: " + metadata.countryOfOrigin);
                    }"""

content = content.replace(old_country_logic, genre_logic)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MangaDetailActivity for Chips")
