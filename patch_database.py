import re

with open("app/src/main/java/com/fire/mangareader/data/local/AppDatabase.kt", "r") as f:
    content = f.read()

content = content.replace("import com.fire.mangareader.data.local.dao.NotificationDao",
    "import com.fire.mangareader.data.local.dao.NotificationDao\nimport com.fire.mangareader.data.local.entity.CustomListEntity\nimport com.fire.mangareader.data.local.entity.CustomListMangaCrossRef\nimport com.fire.mangareader.data.local.dao.CustomListDao")

content = content.replace("@Database(entities = [CommentEntity::class, FavoriteManga::class, RecentManga::class, DownloadedChapter::class, NotificationEntity::class], version = 5, exportSchema = false)",
    "@Database(entities = [CommentEntity::class, FavoriteManga::class, RecentManga::class, DownloadedChapter::class, NotificationEntity::class, CustomListEntity::class, CustomListMangaCrossRef::class], version = 6, exportSchema = false)")

content = content.replace("abstract fun notificationDao(): NotificationDao",
    "abstract fun notificationDao(): NotificationDao\n    abstract fun customListDao(): CustomListDao")

with open("app/src/main/java/com/fire/mangareader/data/local/AppDatabase.kt", "w") as f:
    f.write(content)
