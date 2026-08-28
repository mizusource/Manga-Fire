with open('app/src/main/java/com/fire/mangareader/database/MangaDao.java', 'r') as f:
    content = f.read()

content = content.replace('''    @Query("SELECT * FROM library WHERE isFavorite = 1")
    java.util.List<com.fire.mangareader.database.LibraryItem> getAllFavorites();''',
'''    @Query("SELECT * FROM library WHERE isFavorite = 1")
    java.util.List<com.fire.mangareader.database.LibraryItem> getAllFavorites();

    @Query("SELECT * FROM library")
    java.util.List<com.fire.mangareader.database.LibraryItem> getAllItems();''')

with open('app/src/main/java/com/fire/mangareader/database/MangaDao.java', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'r') as f:
    lib_content = f.read()

lib_content = lib_content.replace('''List<LibraryItem> items = AppDatabase.getInstance(this).mangaDao().getAllFavorites();''',
'''List<LibraryItem> items = AppDatabase.getInstance(this).mangaDao().getAllItems();''')

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'w') as f:
    f.write(lib_content)
