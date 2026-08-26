import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

comments_btn_old = """        btnComments.setOnClickListener(v -> {
            Intent intent = new Intent(MangaDetailActivity.this, CommentsActivity.class);
            intent.putExtra("mangaUrl", mangaUrl);
                intent.putExtra("mangaTitle", mangaTitle);
                intent.putExtra("mangaCover", mangaCover);
            startActivity(intent);
        });"""

comments_btn_new = """        btnComments.setOnClickListener(v -> {
            CommentsBottomSheetDialog bottomSheet = new CommentsBottomSheetDialog(mangaUrl);
            bottomSheet.show(getSupportFragmentManager(), "CommentsBottomSheet");
        });"""

content = content.replace(comments_btn_old, comments_btn_new)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
