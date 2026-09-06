with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

bad_lines = """            android.widget.TextView tvFavorite = sheetView.findViewById(R.id.statusFavorite);
            if (tvFavorite != null) {
                tvFavorite.setOnClickListener(v -> {
                    updateLibraryStatus("favorite");
                    bottomSheetDialog.dismiss();
                });
            }"""
content = content.replace(bad_lines, "")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)

