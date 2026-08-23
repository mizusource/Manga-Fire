sed -i '/private FloatingActionButton btnRead;/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/btnRead = findViewById(R.id.btnRead);/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/btnRead.setOnClickListener/,+17d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/private void updateSmartReadButton/,+20d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/updateSmartReadButton(states);/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/btnRead.setBackgroundTintList/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/com.google.android.material.appbar.CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/if (collapsingToolbar != null) {/,+3d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
