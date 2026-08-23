sed -i 's/FloatingActionButton fabFavorite/ImageView btnFavorite/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/FloatingActionButton fabComments/ImageView btnComments/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/fabFavorite = findViewById(R.id.fabFavorite);/btnFavorite = findViewById(R.id.btnFavorite);/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/fabComments = findViewById(R.id.fabComments);/btnComments = findViewById(R.id.btnComments);/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/fabFavorite\.setOnClickListener/btnFavorite.setOnClickListener/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/fabComments\.setOnClickListener/btnComments.setOnClickListener/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/titleText = findViewById(R.id.mangaTitle);/titleText = findViewById(R.id.toolbarTitle);\n        TextView mangaTitleDetail = findViewById(R.id.mangaTitleDetail);\n        if (mangaTitleDetail != null) mangaTitleDetail.setText(mangaTitle);/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/FloatingActionButton btnRead = findViewById(R.id.btnRead);/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/if (btnRead != null) {/,/}/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/com.google.android.material.floatingactionbutton.FloatingActionButton fabFavorite = findViewById(R.id.fabFavorite);/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/if (fabFavorite != null) {/,/}/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
