import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

import_statement = "import android.content.Intent\nimport com.fire.mangareader.presentation.activity.MangaDetailActivity\n"
if "import android.content.Intent" not in content:
    content = content.replace("import android.os.Bundle", import_statement + "import android.os.Bundle")

content = content.replace(
    'onMangaClick = { mangaId -> navController.navigate("detail/$mangaId") }',
    '''onMangaClick = { mangaId, mangaTitle, mangaCover -> 
                                    val intent = Intent(this@MainComposeActivity, MangaDetailActivity::class.java).apply {
                                        putExtra("mangaUrl", mangaId)
                                        putExtra("mangaTitle", mangaTitle)
                                        putExtra("mangaCover", mangaCover)
                                    }
                                    startActivity(intent)
                                }'''
)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)

