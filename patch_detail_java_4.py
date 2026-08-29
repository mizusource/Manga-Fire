import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

content = content.replace('''                runOnUiThread(() -> {
                    // TextView tvAniListAuthor
                    // if("جاري الجلب")) {
                        // tvAniListAuthor("المؤلف: غير متوفر");
                    }
                });''', '''                runOnUiThread(() -> {
                    // Ignored
                });''')

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

