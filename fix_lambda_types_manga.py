import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

content = content.replace(
    '''onMangaClick = { mangaId, mangaTitle, mangaCover ->''',
    '''onMangaClick = { mangaId: String, mangaTitle: String, mangaCover: String ->'''
)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
