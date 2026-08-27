import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Replace variables
content = content.replace('private ImageView btnFavorite, btnComments;', 
'''private ImageView btnFavorite, btnComments;
    private android.widget.LinearLayout btnFavoriteContainer, btnCommentsContainer;
    private TextView tvFavoriteText;''')

# Find ViewByIds
content = content.replace('btnFavorite = findViewById(R.id.btnFavorite);',
'''btnFavorite = findViewById(R.id.btnFavorite);
        btnFavoriteContainer = findViewById(R.id.btnFavoriteContainer);
        btnCommentsContainer = findViewById(R.id.btnCommentsContainer);
        tvFavoriteText = findViewById(R.id.tvFavoriteText);''')

# Fix btnComments click listener
old_btn_comments_click = '''        btnComments.setOnClickListener(v -> {
            CommentsBottomSheetDialog bottomSheet = new CommentsBottomSheetDialog(mangaUrl);
            bottomSheet.show(getSupportFragmentManager(), "CommentsBottomSheet");
        });'''
new_btn_comments_click = '''        if (btnCommentsContainer != null) {
            btnCommentsContainer.setOnClickListener(v -> {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    CommentsBottomSheetDialog bottomSheet = new CommentsBottomSheetDialog(mangaUrl);
                    bottomSheet.show(getSupportFragmentManager(), "CommentsBottomSheet");
                }).start();
            });
        } else {
            btnComments.setOnClickListener(v -> {
                CommentsBottomSheetDialog bottomSheet = new CommentsBottomSheetDialog(mangaUrl);
                bottomSheet.show(getSupportFragmentManager(), "CommentsBottomSheet");
            });
        }'''
content = content.replace(old_btn_comments_click, new_btn_comments_click)

# Update checkFavoriteStatus callback
old_fav_status = '''                        isFavorite = true;
                        btnFavorite.setImageResource(R.drawable.ic_favorite);
                        btnFavorite.setColorFilter(android.graphics.Color.RED);
                    } else {
                        isFavorite = false;
                        btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                        btnFavorite.setColorFilter(null);
                    }'''
new_fav_status = '''                        isFavorite = true;
                        btnFavorite.setImageResource(R.drawable.ic_favorite);
                        btnFavorite.setColorFilter(android.graphics.Color.RED);
                        if (tvFavoriteText != null) { tvFavoriteText.setText("محفوظ"); tvFavoriteText.setTextColor(android.graphics.Color.RED); }
                    } else {
                        isFavorite = false;
                        btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                        btnFavorite.setColorFilter(null);
                        if (tvFavoriteText != null) { tvFavoriteText.setText("حفظ"); tvFavoriteText.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme())); }
                    }'''
content = content.replace(old_fav_status, new_fav_status)

# Update toggleFavorite logic
old_toggle_fav = '''btnFavorite.setOnClickListener(v -> {'''
new_toggle_fav = '''android.view.View.OnClickListener favClickListener = v -> {
            android.view.View targetView = btnFavoriteContainer != null ? btnFavoriteContainer : btnFavorite;
            targetView.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                targetView.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                
                FirebaseUser user = mAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(MangaDetailActivity.this, "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                String docId = mangaUrl.replaceAll("[^a-zA-Z0-9]", "_");
                if (isFavorite) {
                    db.collection("users").document(user.getUid()).collection("favorites").document(docId).delete()
                        .addOnSuccessListener(aVoid -> {
                            isFavorite = false;
                            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                            btnFavorite.setColorFilter(null);
                            if (tvFavoriteText != null) { tvFavoriteText.setText("حفظ"); tvFavoriteText.setTextColor(getResources().getColor(R.color.colorPrimary, getTheme())); }
                            Toast.makeText(MangaDetailActivity.this, "تمت الإزالة من المفضلة", Toast.LENGTH_SHORT).show();
                        });
                } else {
                    java.util.Map<String, Object> fav = new java.util.HashMap<>();
                    fav.put("mangaUrl", mangaUrl);
                    fav.put("title", mangaTitle);
                    fav.put("coverUrl", mangaCover);
                    fav.put("timestamp", com.google.firebase.firestore.FieldValue.serverTimestamp());
                    db.collection("users").document(user.getUid()).collection("favorites").document(docId).set(fav)
                        .addOnSuccessListener(aVoid -> {
                            isFavorite = true;
                            btnFavorite.setImageResource(R.drawable.ic_favorite);
                            btnFavorite.setColorFilter(android.graphics.Color.RED);
                            if (tvFavoriteText != null) { tvFavoriteText.setText("محفوظ"); tvFavoriteText.setTextColor(android.graphics.Color.RED); }
                            Toast.makeText(MangaDetailActivity.this, "تمت الإضافة للمفضلة", Toast.LENGTH_SHORT).show();
                        });
                }
            }).start();
        };
        if (btnFavoriteContainer != null) {
            btnFavoriteContainer.setOnClickListener(favClickListener);
        } else {
            btnFavorite.setOnClickListener(favClickListener);
        }

        // We replace the old listener by commenting it out, or we just leave the placeholder for python regex.
        /*'''
content = content.replace(old_toggle_fav, new_toggle_fav)

# Now we must terminate the multiline comment before `View btnMyList` or similar.
# Find where the old btnFavorite.setOnClickListener ends.
old_toggle_end = '''Toast.makeText(MangaDetailActivity.this, "تمت الإضافة للمفضلة", Toast.LENGTH_SHORT).show();
                    });
            }
        });'''
new_toggle_end = '''Toast.makeText(MangaDetailActivity.this, "تمت الإضافة للمفضلة", Toast.LENGTH_SHORT).show();
                    });
            }
        });
        */'''
content = content.replace(old_toggle_end, new_toggle_end)


with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
