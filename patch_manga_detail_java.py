with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Replace old button IDs with new ones
content = content.replace('R.id.btnSave', 'R.id.btnChangeStatus')
content = content.replace('R.id.coverImageBlur', 'R.id.mangaCoverBlur')
content = content.replace('R.id.coverImage', 'R.id.mangaCover')

if "mangaAuthor =" not in content:
    # Let's bind mangaAuthor if it exists. Actually, I can just use a try-catch for view binding.
    content = content.replace(
        'TextView tvTitle = findViewById(R.id.mangaTitle);',
        'TextView tvTitle = findViewById(R.id.mangaTitle);\n        TextView tvAuthor = findViewById(R.id.mangaAuthor);'
    )
    content = content.replace(
        'tvTitle.setText(mangaTitle);',
        'tvTitle.setText(mangaTitle);\n        if (tvAuthor != null) tvAuthor.setText("المؤلف: غير معروف");'
    )

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
