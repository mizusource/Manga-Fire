import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/CustomListDetailActivity.java", "r") as f:
    content = f.read()

content = content.replace("import com.fire.mangareader.data.database.Manga;", "import com.fire.mangareader.domain.model.Manga;")
content = content.replace("com.fire.mangareader.data.local.com.fire.mangareader.data.database.AppDatabase", "com.fire.mangareader.data.database.AppDatabase")
content = content.replace("com.fire.mangareader.data.local.AppDatabase.Companion.getDatabase(this).mangaDao()", "com.fire.mangareader.data.database.AppDatabase.getInstance(this).mangaDao()")


with open("app/src/main/java/com/fire/mangareader/presentation/activity/CustomListDetailActivity.java", "w") as f:
    f.write(content)

