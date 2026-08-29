import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Add currentLibraryStatus field
if 'private String currentLibraryStatus = "";' not in content:
    content = content.replace('private boolean isFavorite = false;', 'private boolean isFavorite = false;\n    private String currentLibraryStatus = "";')

# Replace checkFavoriteStatus
new_check = """
    private void checkFavoriteStatus() {
        if (!com.fire.mangareader.network.SupabaseManager.getInstance(this).isLoggedIn()) {
            runOnUiThread(() -> {
                if (btnFavorite != null) {
                    btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                    btnFavorite.setColorFilter(android.graphics.Color.GRAY);
                }
                isFavorite = false;
                currentLibraryStatus = "";
            });
            return;
        }

        com.fire.mangareader.network.SupabaseManager.getInstance(this).checkLibraryStatus(mangaUrl, new com.fire.mangareader.network.SupabaseManager.DataCallback() {
            @Override
            public void onSuccess(org.json.JSONArray data) {
                boolean inLibrary = data != null && data.length() > 0;
                String status = "";
                if (inLibrary) {
                    try {
                        status = data.getJSONObject(0).getString("status");
                    } catch (Exception e) {}
                }
                
                final boolean isFav = inLibrary;
                final String finalStatus = status;
                
                runOnUiThread(() -> {
                    if (btnFavorite != null) {
                        btnFavorite.setImageResource(isFav ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                        btnFavorite.setColorFilter(isFav ? android.graphics.Color.RED : android.graphics.Color.GRAY);
                    }
                    isFavorite = isFav;
                    currentLibraryStatus = finalStatus;
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    if (btnFavorite != null) {
                        btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                        btnFavorite.setColorFilter(android.graphics.Color.GRAY);
                    }
                    isFavorite = false;
                    currentLibraryStatus = "";
                });
            }
        });
    }
"""
content = re.sub(r'private void checkFavoriteStatus\(\)\s*\{.*?\n    \}', new_check.strip(), content, flags=re.DOTALL)

# Replace toggleFavorite
new_toggle = """
    private void toggleFavorite() {
        if (!com.fire.mangareader.network.SupabaseManager.getInstance(this).isLoggedIn()) {
            Toast.makeText(this, "يجب تسجيل الدخول لإضافة المانجا للمكتبة", Toast.LENGTH_SHORT).show();
            return;
        }
        
        android.view.View targetView = btnFavoriteContainer != null ? btnFavoriteContainer : btnFavorite;
        targetView.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
            targetView.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            
            String[] options = {"أقرأها حالياً", "أرغب بمشاهدتها", "مكتملة", "مفضلة", "إزالة من القائمة"};
            String[] statusValues = {"reading", "plan_to_read", "completed", "favorite", "remove"};
            
            int checkedItem = -1;
            if (currentLibraryStatus.equals("reading")) checkedItem = 0;
            else if (currentLibraryStatus.equals("plan_to_read")) checkedItem = 1;
            else if (currentLibraryStatus.equals("completed")) checkedItem = 2;
            else if (currentLibraryStatus.equals("favorite")) checkedItem = 3;
            
            new androidx.appcompat.app.AlertDialog.Builder(MangaDetailActivity.this)
                    .setTitle("إضافة إلى المكتبة")
                    .setSingleChoiceItems(options, checkedItem, (dialog, which) -> {
                        dialog.dismiss();
                        if (which == 4) {
                            // Remove
                            com.fire.mangareader.network.SupabaseManager.getInstance(MangaDetailActivity.this).removeFromLibrary(mangaUrl, new com.fire.mangareader.network.SupabaseManager.AuthCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    isFavorite = false;
                                    currentLibraryStatus = "";
                                    btnFavorite.setImageResource(R.drawable.ic_favorite_border);
                                    btnFavorite.setColorFilter(android.graphics.Color.GRAY);
                                    Toast.makeText(MangaDetailActivity.this, "تمت الإزالة من المكتبة", Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onError(String error) {
                                    Toast.makeText(MangaDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            String selectedStatus = statusValues[which];
                            com.fire.mangareader.network.SupabaseManager.getInstance(MangaDetailActivity.this).addToLibrary(mangaUrl, mangaTitle, mangaCover, selectedStatus, new com.fire.mangareader.network.SupabaseManager.AuthCallback() {
                                @Override
                                public void onSuccess(String message) {
                                    isFavorite = true;
                                    currentLibraryStatus = selectedStatus;
                                    btnFavorite.setImageResource(R.drawable.ic_favorite);
                                    btnFavorite.setColorFilter(android.graphics.Color.RED);
                                    Toast.makeText(MangaDetailActivity.this, options[which], Toast.LENGTH_SHORT).show();
                                }
                                @Override
                                public void onError(String error) {
                                    Toast.makeText(MangaDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .show();
        }).start();
    }
"""
content = re.sub(r'private void toggleFavorite\(\)\s*\{.*?\n    \}', new_toggle.strip(), content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

