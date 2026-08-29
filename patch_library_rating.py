import re

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'r') as f:
    content = f.read()

content = content.replace('manga.setRating(status);', 'manga.setRating("❤️");')

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'w') as f:
    f.write(content)
