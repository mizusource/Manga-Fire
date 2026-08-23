sed -i 's/getMangaById/getItemById/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/com.google.android.material.floatingactionbutton.ImageView/ImageView/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/findViewById(R.id.fabFavorite);/findViewById(R.id.btnFavorite);/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/View swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);/ /g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i 's/swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);/swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);/g' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
