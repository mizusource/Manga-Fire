import os

filepath = 'app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Enable format text
content = content.replace("if (false && metadata.format != null) {", "if (metadata.format != null) {")
content = content.replace('// tvAniListFormat(metadata.format);', 'TextView tvFormat = findViewById(R.id.tvAniListFormat);\n                        if (tvFormat != null) tvFormat.setText("النوع: " + metadata.format);')

content = content.replace('// TextView tvAniListFormat', 'TextView tvAniListFormat = findViewById(R.id.tvAniListFormat);')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MangaDetailActivity")
