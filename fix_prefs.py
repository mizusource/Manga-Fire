with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'r') as f:
    content = f.read()

content = content.replace('com.fire.mangareader.utils.PreferenceManager.getInstance(this).getUserName()', 'new com.fire.mangareader.utils.PreferenceManager(this).getUserName()')

with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'w') as f:
    f.write(content)
