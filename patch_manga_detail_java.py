import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    text = f.read()

# Replace toggleFavorite logic to use bottom_sheet_my_list.xml
old_logic = r'String\[\] options = \{"أقرأها حالياً", "أرغب بمشاهدتها", "مكتملة", "مفضلة", "إزالة من القائمة"\};[\s\S]*?\}\)\s*\.show\(\);'

new_logic = """
            com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(MangaDetailActivity.this);
            android.view.View sheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_my_list, null);
            bottomSheetDialog.setContentView(sheetView);
            
            android.widget.TextView tvWatching = sheetView.findViewById(R.id.statusWatching);
            android.widget.TextView tvPlan = sheetView.findViewById(R.id.statusPlan);
            android.widget.TextView tvCompleted = sheetView.findViewById(R.id.statusCompleted);
            android.widget.TextView tvDropped = sheetView.findViewById(R.id.statusDropped);
            
            if (currentLibraryStatus.equals("reading")) tvWatching.setTextColor(android.graphics.Color.parseColor("#E91E63"));
            else if (currentLibraryStatus.equals("plan_to_read")) tvPlan.setTextColor(android.graphics.Color.parseColor("#E91E63"));
            else if (currentLibraryStatus.equals("completed")) tvCompleted.setTextColor(android.graphics.Color.parseColor("#E91E63"));
            else if (currentLibraryStatus.equals("dropped")) tvDropped.setTextColor(android.graphics.Color.parseColor("#E91E63"));
            
            android.view.View.OnClickListener listener = v -> {
                String selectedStatus = "";
                if (v.getId() == R.id.statusWatching) selectedStatus = "reading";
                else if (v.getId() == R.id.statusPlan) selectedStatus = "plan_to_read";
                else if (v.getId() == R.id.statusCompleted) selectedStatus = "completed";
                else if (v.getId() == R.id.statusDropped) selectedStatus = "dropped";
                
                bottomSheetDialog.dismiss();
                
                com.fire.mangareader.data.network.SupabaseManager.getInstance(MangaDetailActivity.this).addToLibrary(mangaUrl, mangaTitle, mangaCover, selectedStatus, new com.fire.mangareader.data.network.SupabaseManager.AuthCallback() {
                    @Override
                    public void onSuccess(String message) {
                        isFavorite = true;
                        currentLibraryStatus = selectedStatus;
                        btnFavorite.setImageResource(R.drawable.ic_favorite);
                        btnFavorite.setColorFilter(android.graphics.Color.RED);
                        Toast.makeText(MangaDetailActivity.this, "تمت الإضافة لقائمتك", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onError(String error) {
                        Toast.makeText(MangaDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            };
            
            tvWatching.setOnClickListener(listener);
            tvPlan.setOnClickListener(listener);
            tvCompleted.setOnClickListener(listener);
            tvDropped.setOnClickListener(listener);
            
            bottomSheetDialog.show();
"""

text = re.sub(old_logic, new_logic.strip(), text)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(text)
