filepath = 'app/src/main/java/com/fire/mangareader/data/local/AppDatabase.kt'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('import androidx.room.RoomDatabase',
                          'import androidx.room.RoomDatabase\nimport com.fire.mangareader.data.local.entity.FavoriteManga\nimport com.fire.mangareader.data.local.entity.RecentManga\nimport com.fire.mangareader.data.local.dao.FavoriteDao\nimport com.fire.mangareader.data.local.dao.RecentDao')

content = content.replace('@Database(entities = [CommentEntity::class], version = 2',
                          '@Database(entities = [CommentEntity::class, FavoriteManga::class, RecentManga::class], version = 3')

content = content.replace('abstract fun commentDao(): CommentDao',
                          'abstract fun commentDao(): CommentDao\n    abstract fun favoriteDao(): FavoriteDao\n    abstract fun recentDao(): RecentDao')

with open(filepath, 'w') as f:
    f.write(content)
