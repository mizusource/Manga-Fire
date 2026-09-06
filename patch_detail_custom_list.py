import re

java_path = "app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java"
with open(java_path, "r") as f:
    content = f.read()

new_custom_list = """
            android.widget.TextView tvCustomList = sheetView.findViewById(R.id.statusCustomList);
            if (tvCustomList != null) {
                tvCustomList.setOnClickListener(v -> {
                    bottomSheetDialog.dismiss();
                    openCustomListManager();
                });
            }
"""

content = content.replace("android.view.View.OnClickListener statusClickListener = v -> {", new_custom_list + "\n            android.view.View.OnClickListener statusClickListener = v -> {")

open_method = """
    private void openCustomListManager() {
        android.content.Intent intent = new android.content.Intent(this, CustomListManagerActivity.class);
        intent.putExtra("mangaUrl", mangaUrl);
        intent.putExtra("mangaTitle", mangaTitle);
        intent.putExtra("mangaCover", mangaCover);
        startActivity(intent);
    }
"""

content = content.replace("private void checkNotificationStatus() {", open_method + "\n    private void checkNotificationStatus() {")

with open(java_path, "w") as f:
    f.write(content)

