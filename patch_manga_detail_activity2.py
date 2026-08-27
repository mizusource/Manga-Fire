import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Fix checkFavoriteStatus
old_check = '''    private void checkFavoriteStatus() {
        new Thread(() -> {
            isFavorite = AppDatabase.getInstance(this).mangaDao().isFavorite(mangaUrl);
            runOnUiThread(() -> btnFavorite.setImageResource(isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off));
        }).start();
    }'''
new_check = '''    private void checkFavoriteStatus() {
        new Thread(() -> {
            isFavorite = AppDatabase.getInstance(this).mangaDao().isFavorite(mangaUrl);
            runOnUiThread(() -> {
                btnFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                btnFavorite.setColorFilter(isFavorite ? android.graphics.Color.RED : getResources().getColor(R.color.colorPrimary, getTheme()));
                if (tvFavoriteText != null) {
                    tvFavoriteText.setText(isFavorite ? "محفوظ" : "حفظ");
                    tvFavoriteText.setTextColor(isFavorite ? android.graphics.Color.RED : getResources().getColor(R.color.colorPrimary, getTheme()));
                }
            });
        }).start();
    }'''
content = content.replace(old_check, new_check)

# Fix toggleFavorite
old_toggle = '''    private void toggleFavorite() {
        isFavorite = !isFavorite;
        btnFavorite.setImageResource(isFavorite ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
        new Thread(() -> {
            LibraryItem item = new LibraryItem();
            item.setMangaId(mangaUrl);
            item.setTitle(mangaTitle);
            item.setCoverUrl(mangaCover);
            item.setFavorite(isFavorite);
            item.setAddedTime(System.currentTimeMillis());
            if (isFavorite) AppDatabase.getInstance(this).mangaDao().insert(item);
            else {
                AppDatabase.getInstance(this).mangaDao().setFavorite(mangaUrl, false);
                AppDatabase.getInstance(this).mangaDao().cleanOrphans();
            }
        }).start();
        Toast.makeText(this, isFavorite ? "تمت الإضافة للمفضلة" : "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
    }'''
new_toggle = '''    private void toggleFavorite() {
        android.view.View targetView = btnFavoriteContainer != null ? btnFavoriteContainer : btnFavorite;
        targetView.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
            targetView.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            isFavorite = !isFavorite;
            
            btnFavorite.setImageResource(isFavorite ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            btnFavorite.setColorFilter(isFavorite ? android.graphics.Color.RED : getResources().getColor(R.color.colorPrimary, getTheme()));
            if (tvFavoriteText != null) {
                tvFavoriteText.setText(isFavorite ? "محفوظ" : "حفظ");
                tvFavoriteText.setTextColor(isFavorite ? android.graphics.Color.RED : getResources().getColor(R.color.colorPrimary, getTheme()));
            }
            
            new Thread(() -> {
                LibraryItem item = new LibraryItem();
                item.setMangaId(mangaUrl);
                item.setTitle(mangaTitle);
                item.setCoverUrl(mangaCover);
                item.setFavorite(isFavorite);
                item.setAddedTime(System.currentTimeMillis());
                if (isFavorite) AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().insert(item);
                else {
                    AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().setFavorite(mangaUrl, false);
                    AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().cleanOrphans();
                }
            }).start();
            Toast.makeText(MangaDetailActivity.this, isFavorite ? "تمت الإضافة للمفضلة" : "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
        }).start();
    }'''
content = content.replace(old_toggle, new_toggle)

# Fix onCreate listener
old_oncreate_click = '''        btnFavorite.setOnClickListener(v -> toggleFavorite());'''
new_oncreate_click = '''        if (btnFavoriteContainer != null) {
            btnFavoriteContainer.setOnClickListener(v -> toggleFavorite());
        } else {
            btnFavorite.setOnClickListener(v -> toggleFavorite());
        }'''
content = content.replace(old_oncreate_click, new_oncreate_click)


with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
