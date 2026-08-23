sed -i '/setContentView(R.layout.activity_comments);/a \
        ImageView btnBack = findViewById(R.id.btnBack);\n        if (btnBack != null) btnBack.setOnClickListener(v -> finish());' app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java
sed -i 's/CheckBox cbSpoiler = findViewById(R.id.cbSpoiler);/android.view.View cbSpoiler = null;/g' app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java
sed -i 's/boolean isSpoiler = cbSpoiler != null && cbSpoiler.isChecked();/boolean isSpoiler = false;/g' app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java
sed -i 's/cbSpoiler.setChecked(false);/ /g' app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java
