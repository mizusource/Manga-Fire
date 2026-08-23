sed -i '1s/^/import android.widget.ImageView;\n/' app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java
sed -i '/cbSpoiler = findViewById(R.id.cbSpoiler);/d' app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java
sed -i '/public static final String SOURCE_MANGATEK =/d' app/src/main/java/com/fire/mangareader/network/SourceManager.java
sed -i '/public static final String SOURCE_MANGASID =/d' app/src/main/java/com/fire/mangareader/network/SourceManager.java
sed -i '/if (url.equals(SOURCE_MANGATEK)) return/d' app/src/main/java/com/fire/mangareader/network/SourceManager.java
sed -i '/if (url.equals(SOURCE_MANGASID)) return/d' app/src/main/java/com/fire/mangareader/network/SourceManager.java
sed -i '/public static final String SOURCE_MANGA_STARZ/a \    public static final String SOURCE_MANGATEK = "https:\/\/mangatek.com\/";\n    public static final String SOURCE_MANGASID = "https:\/\/mangasid.com\/";' app/src/main/java/com/fire/mangareader/network/SourceManager.java
sed -i '/if (url.equals(SOURCE_MANGA_STARZ)) return "Manga-Starz";/a \        if (url.equals(SOURCE_MANGATEK)) return "Mangatek";\n        if (url.equals(SOURCE_MANGASID)) return "Mangasid";' app/src/main/java/com/fire/mangareader/network/SourceManager.java
