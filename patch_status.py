import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

old_update = '''    private void updateMangaStatus(String status) {
        new Thread(() -> {
            LibraryItem item = AppDatabase.getInstance(this).mangaDao().getItemById(mangaUrl);
            if (item == null) {
                item = new LibraryItem();
                item.setMangaId(mangaUrl);
                item.setTitle(mangaTitle);
                item.setCoverUrl(mangaCover);
            }
            item.setStatus(status);
            item.setFavorite(true);
            AppDatabase.getInstance(this).mangaDao().insert(item);
            
            isFavorite = true;
            runOnUiThread(() -> {
                ImageView btnFavorite = findViewById(R.id.btnFavorite);
        btnFavoriteContainer = findViewById(R.id.btnFavoriteContainer);
        btnCommentsContainer = findViewById(R.id.btnCommentsContainer);
        tvFavoriteText = findViewById(R.id.tvFavoriteText);
                if (btnFavorite != null) btnFavorite.setImageResource(android.R.drawable.btn_star_big_on);
                android.widget.Toast.makeText(MangaDetailActivity.this, "تمت الإضافة إلى: " + status, android.widget.Toast.LENGTH_SHORT).show();
            });
        }).start();
    }'''

new_update = '''    private void updateMangaStatus(String status) {
        new Thread(() -> {
            LibraryItem item = AppDatabase.getInstance(this).mangaDao().getItemById(mangaUrl);
            if (item == null) {
                item = new LibraryItem();
                item.setMangaId(mangaUrl);
                item.setTitle(mangaTitle);
                item.setCoverUrl(mangaCover);
            }
            item.setStatus(status);
            item.setFavorite(true);
            AppDatabase.getInstance(this).mangaDao().insert(item);
            
            isFavorite = true;
            runOnUiThread(() -> {
                ImageView btnFavorite = findViewById(R.id.btnFavorite);
                if (btnFavorite != null) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                    btnFavorite.setColorFilter(android.graphics.Color.RED);
                }
                if (tvFavoriteText != null) {
                    tvFavoriteText.setText("محفوظ");
                    tvFavoriteText.setTextColor(android.graphics.Color.RED);
                }
                
                android.widget.TextView tvMyListStatus = findViewById(R.id.tvMyListStatus);
                if (tvMyListStatus != null) {
                    tvMyListStatus.setText(status);
                    tvMyListStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"));
                }
                
                android.widget.Toast.makeText(MangaDetailActivity.this, "تمت الإضافة إلى: " + status, android.widget.Toast.LENGTH_SHORT).show();
            });
        }).start();
    }'''
content = content.replace(old_update, new_update)

old_check = '''    private void checkFavoriteStatus() {
        new Thread(() -> {
            isFavorite = AppDatabase.getInstance(this).mangaDao().isFavorite(mangaUrl);
            runOnUiThread(() -> {
                btnFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                btnFavorite.setColorFilter(isFavorite ? android.graphics.Color.RED : android.graphics.Color.GRAY);
                if (tvFavoriteText != null) {
                    tvFavoriteText.setText(isFavorite ? "محفوظ" : "حفظ");
                    tvFavoriteText.setTextColor(isFavorite ? android.graphics.Color.RED : android.graphics.Color.GRAY);
                }
            });
        }).start();
    }'''

new_check = '''    private void checkFavoriteStatus() {
        new Thread(() -> {
            LibraryItem item = AppDatabase.getInstance(this).mangaDao().getItemById(mangaUrl);
            isFavorite = (item != null && item.isFavorite());
            String status = (item != null && item.getStatus() != null) ? item.getStatus() : "غير مضاف";
            runOnUiThread(() -> {
                if (btnFavorite != null) {
                    btnFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                    btnFavorite.setColorFilter(isFavorite ? android.graphics.Color.RED : android.graphics.Color.GRAY);
                }
                if (tvFavoriteText != null) {
                    tvFavoriteText.setText(isFavorite ? "محفوظ" : "حفظ");
                    tvFavoriteText.setTextColor(isFavorite ? android.graphics.Color.RED : android.graphics.Color.GRAY);
                }
                android.widget.TextView tvMyListStatus = findViewById(R.id.tvMyListStatus);
                if (tvMyListStatus != null) {
                    tvMyListStatus.setText(status);
                    if (!status.equals("غير مضاف")) {
                        tvMyListStatus.setTextColor(android.graphics.Color.parseColor("#FF9800"));
                    }
                }
            });
        }).start();
    }'''
content = content.replace(old_check, new_check)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

