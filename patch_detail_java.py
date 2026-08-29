import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Fix toolbarTitle
content = re.sub(r'titleText = findViewById\(R\.id\.toolbarTitle\);', r'// titleText = findViewById(R.id.toolbarTitle);', content)
content = re.sub(r'titleText\.setText\(mangaTitle\);', r'// titleText.setText(mangaTitle);', content)

# Fix btnFavoriteContainer
content = re.sub(r'btnFavoriteContainer = findViewById\(R\.id\.btnFavoriteContainer\);', r'// btnFavoriteContainer = findViewById(R.id.btnFavoriteContainer);', content)

# Fix btnCommentsContainer -> btnComments
content = re.sub(r'btnCommentsContainer = findViewById\(R\.id\.btnCommentsContainer\);', r'// btnCommentsContainer', content)
content = re.sub(r'btnCommentsContainer != null \? btnCommentsContainer : btnComments', r'btnComments', content)

# Fix tvFavoriteText
content = re.sub(r'tvFavoriteText = findViewById\(R\.id\.tvFavoriteText\);', r'// tvFavoriteText', content)
content = re.sub(r'if \(tvFavoriteText != null\) {[\s\S]*?}', r'', content)

# Fix setOnClickListener for btnCommentsContainer
content = re.sub(r'if \(btnCommentsContainer != null\) {\s*btnCommentsContainer\.setOnClickListener', r'if (btnComments != null) { btnComments.setOnClickListener', content)

# Also bind btnStartReading
btn_start_reading_code = """
        android.widget.Button btnStartReading = findViewById(R.id.btnStartReading);
        if (btnStartReading != null) {
            btnStartReading.setOnClickListener(v -> {
                if (chaptersList != null && !chaptersList.isEmpty()) {
                    // Try to find the first unread chapter (or just start from chapter 1)
                    com.fire.mangareader.model.Chapter firstChapter = chaptersList.get(chaptersList.size() - 1);
                    android.content.Intent intent = new android.content.Intent(MangaDetailActivity.this, ChapterReaderActivity.class);
                    intent.putExtra("chapterUrl", firstChapter.getUrl());
                    intent.putExtra("mangaUrl", mangaUrl);
                    intent.putExtra("mangaTitle", mangaTitle);
                    intent.putExtra("mangaCover", mangaCover);
                    intent.putExtra("chapterTitle", firstChapter.getTitle());
                    startActivity(intent);
                } else {
                    android.widget.Toast.makeText(this, "لا توجد فصول متاحة بعد", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }
"""
content = re.sub(r'(ImageView btnBack = findViewById\(R\.id\.btnBack\);)', btn_start_reading_code + r'\n        \1', content)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

