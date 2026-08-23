sed -i 's/db.mangaDao().getLibrary()/db.mangaDao().getAllFavorites()/g' app/src/main/java/com/fire/mangareader/utils/UpdateCheckWorker.java
sed -i 's/item.mangaUrl/item.getMangaId()/g' app/src/main/java/com/fire/mangareader/utils/UpdateCheckWorker.java
sed -i 's/item.title/item.getTitle()/g' app/src/main/java/com/fire/mangareader/utils/UpdateCheckWorker.java
