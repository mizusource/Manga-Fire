import os

filepath = 'app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('TextView tvAniListFormat = findViewById(R.id.tvAniListFormat);', '// TextView tvAniListFormat = findViewById(R.id.tvAniListFormat);')
content = content.replace('TextView tvFormat = findViewById(R.id.tvAniListFormat);\n                        if (tvFormat != null) tvFormat.setText("النوع: " + metadata.format);', '// tvAniListFormat(metadata.format);')

with open(filepath, 'w') as f:
    f.write(content)
print("Undo MangaDetailActivity format view")
