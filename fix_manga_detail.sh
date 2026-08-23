sed -i '/break;/d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
sed -i '/if (nextChapterToRead == null) {/,+2d' app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java
