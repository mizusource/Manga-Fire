import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

bad_comments_logic = """        if (btnComments != null) { btnComments.setOnClickListener(v -> {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    Intent intent = new Intent(MangaDetailActivity.this, CommentsActivity.class);
                    intent.putExtra("mangaUrl", mangaUrl);
                    startActivity(intent);
                }).start();
            });
        } else {
            btnComments.setOnClickListener(v -> {
                Intent intent = new Intent(MangaDetailActivity.this, CommentsActivity.class);
                intent.putExtra("mangaUrl", mangaUrl);
                startActivity(intent);
            });
        }"""

good_comments_logic = """        if (btnComments != null) { 
            btnComments.setOnClickListener(v -> {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    Intent intent = new Intent(MangaDetailActivity.this, CommentsActivity.class);
                    intent.putExtra("mangaUrl", mangaUrl);
                    startActivity(intent);
                }).start();
            });
        }"""

content = content.replace(bad_comments_logic, good_comments_logic)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

