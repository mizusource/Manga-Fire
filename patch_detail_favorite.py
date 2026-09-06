import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

new_toggle = """
    private void toggleFavorite() {
        android.view.View targetView = btnFavoriteContainer != null ? btnFavoriteContainer : btnFavorite;
        targetView.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
            targetView.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
            
            com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(MangaDetailActivity.this);
            android.view.View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_my_list, null);
            bottomSheetDialog.setContentView(sheetView);
            
            android.widget.TextView tvWatching = sheetView.findViewById(R.id.statusWatching);
            android.widget.TextView tvPlan = sheetView.findViewById(R.id.statusPlan);
            android.widget.TextView tvCompleted = sheetView.findViewById(R.id.statusCompleted);
            android.widget.TextView tvDropped = sheetView.findViewById(R.id.statusDropped);
            android.widget.TextView tvFavorite = sheetView.findViewById(R.id.statusFavorite);
            if (tvFavorite == null) {
                // If there's no favorite text view in the layout, let's just use dropped as a placeholder or add a dynamic one
                tvFavorite = tvDropped; // Fallback
            }
            
            if (currentLibraryStatus.equals("reading")) tvWatching.setTextColor(android.graphics.Color.parseColor("#E91E63"));
            else if (currentLibraryStatus.equals("plan_to_read")) tvPlan.setTextColor(android.graphics.Color.parseColor("#E91E63"));
            else if (currentLibraryStatus.equals("completed")) tvCompleted.setTextColor(android.graphics.Color.parseColor("#E91E63"));
            else if (currentLibraryStatus.equals("favorite")) tvFavorite.setTextColor(android.graphics.Color.parseColor("#E91E63"));
            
            android.view.View.OnClickListener statusClickListener = v -> {
                String newStatus = "";
                if (v.getId() == R.id.statusWatching) newStatus = "reading";
                else if (v.getId() == R.id.statusPlan) newStatus = "plan_to_read";
                else if (v.getId() == R.id.statusCompleted) newStatus = "completed";
                else newStatus = "favorite";
                
                String finalStatus = newStatus;
                
                // 1. Update Local DB
                new Thread(() -> {
                    com.fire.mangareader.data.database.LibraryItem item = AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().getItemById(mangaUrl);
                    if (item == null) {
                        item = new com.fire.mangareader.data.database.LibraryItem();
                        item.setMangaId(mangaUrl);
                        item.setTitle(mangaTitle);
                        item.setCoverUrl(mangaCover);
                        item.setAddedTime(System.currentTimeMillis());
                    }
                    if (finalStatus.equals("favorite")) {
                        item.setFavorite(true);
                        // don't overwrite reading status if just favoriting
                    } else {
                        item.setStatus(finalStatus);
                    }
                    AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().insert(item);
                    
                    runOnUiThread(() -> {
                        currentLibraryStatus = finalStatus;
                        updateFavoriteIcon();
                        Toast.makeText(MangaDetailActivity.this, "تم الحفظ في المكتبة", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                    });
                }).start();
                
                // 2. Sync to Supabase if logged in
                if (com.fire.mangareader.data.network.SupabaseManager.getInstance(MangaDetailActivity.this).isLoggedIn()) {
                    com.fire.mangareader.data.network.SupabaseManager.getInstance(MangaDetailActivity.this).addToLibrary(mangaUrl, mangaTitle, mangaCover, finalStatus, new com.fire.mangareader.data.network.SupabaseManager.DataCallback() {
                        @Override
                        public void onSuccess(org.json.JSONArray data) { }
                        @Override
                        public void onError(String error) { }
                    });
                }
            };
            
            tvWatching.setOnClickListener(statusClickListener);
            tvPlan.setOnClickListener(statusClickListener);
            tvCompleted.setOnClickListener(statusClickListener);
            tvDropped.setOnClickListener(statusClickListener);
            
            bottomSheetDialog.show();
        }).start();
    }
"""

content = re.sub(r'private void toggleFavorite\(\) \{.*?\}\)\.start\(\);\n    \}', new_toggle.strip(), content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

