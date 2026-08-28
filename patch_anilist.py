with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

success_block = '''                    if (tvAniListAuthor != null && metadata.author != null) {
                        tvAniListAuthor.setText("المؤلف: " + metadata.author);
                    }'''

success_block_new = '''                    if (tvAniListAuthor != null) {
                        tvAniListAuthor.setText("المؤلف: " + (metadata.author != null && !metadata.author.isEmpty() ? metadata.author : "غير متوفر"));
                    }'''

content = content.replace(success_block, success_block_new)

error_block = '''            public void onError(String errorMessage) {'''
error_block_new = '''            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    TextView tvAniListAuthor = findViewById(R.id.tvAniListAuthor);
                    if (tvAniListAuthor != null && tvAniListAuthor.getText().toString().contains("جاري الجلب")) {
                        tvAniListAuthor.setText("المؤلف: غير متوفر");
                    }
                });'''
content = content.replace(error_block, error_block_new)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
