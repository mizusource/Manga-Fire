import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

btn_custom_list_logic = """
        ImageView btnCustomList = findViewById(R.id.btnCustomList);
        if (btnCustomList != null) {
            btnCustomList.setOnClickListener(v -> {
                v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    showAddToListDialog();
                }).start();
            });
        }
"""

content = content.replace("ImageView btnDownloadMultiple = findViewById(R.id.btnDownloadMultiple);", btn_custom_list_logic + "\n        ImageView btnDownloadMultiple = findViewById(R.id.btnDownloadMultiple);")


func_add_to_list = """
    private void showAddToListDialog() {
        new Thread(() -> {
            try {
                java.util.List<com.fire.mangareader.data.local.entity.CustomListEntity> lists = com.fire.mangareader.data.local.AppDatabase.Companion.getDatabase(this).customListDao().getAllListsSync();
                if (lists == null || lists.isEmpty()) {
                    runOnUiThread(() -> android.widget.Toast.makeText(this, "لا توجد قوائم مخصصة، قم بإنشاء واحدة أولاً.", android.widget.Toast.LENGTH_SHORT).show());
                    return;
                }
                
                String[] listNames = new String[lists.size()];
                for (int i = 0; i < lists.size(); i++) listNames[i] = lists.get(i).getName();
                
                runOnUiThread(() -> {
                    new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("إضافة إلى قائمة مخصصة")
                        .setItems(listNames, (dialog, which) -> {
                            com.fire.mangareader.data.local.entity.CustomListEntity selectedList = lists.get(which);
                            new Thread(() -> {
                                try {
                                    // Ensure manga exists in library first
                                    com.fire.mangareader.data.database.LibraryItem item = AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().getItemById(mangaUrl);
                                    if (item == null) {
                                        item = new com.fire.mangareader.data.database.LibraryItem();
                                        item.setMangaId(mangaUrl);
                                        item.setTitle(mangaTitle);
                                        item.setCoverUrl(mangaCover);
                                        AppDatabase.getInstance(MangaDetailActivity.this).mangaDao().insert(item);
                                    }
                                    
                                    com.fire.mangareader.data.local.entity.CustomListMangaCrossRef crossRef = new com.fire.mangareader.data.local.entity.CustomListMangaCrossRef(selectedList.getListId(), mangaUrl);
                                    com.fire.mangareader.data.local.AppDatabase.Companion.getDatabase(MangaDetailActivity.this).customListDao().insertMangaToList(crossRef);
                                    
                                    runOnUiThread(() -> android.widget.Toast.makeText(MangaDetailActivity.this, "تمت الإضافة إلى " + selectedList.getName(), android.widget.Toast.LENGTH_SHORT).show());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        })
                        .show();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
"""

if "showAddToListDialog" not in content:
    content = content.replace("private void openCustomListManager() {", func_add_to_list + "\n    private void openCustomListManager() {")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

