with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

content = content.replace('com.fire.mangareader.network.DownloadService', 'com.fire.mangareader.service.DownloadService')

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
