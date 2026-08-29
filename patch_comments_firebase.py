import re
import os

def replace_in_file(path, old, new):
    if not os.path.exists(path): return
    with open(path, 'r') as f:
        content = f.read()
    content = content.replace(old, new)
    with open(path, 'w') as f:
        f.write(content)

# GlobalMangaStatsManager.java
with open('app/src/main/java/com/fire/mangareader/utils/GlobalMangaStatsManager.java', 'r') as f:
    content = f.read()
content = re.sub(r'private static FirebaseFirestore db = FirebaseFirestore\.getInstance\(\);', 'private static Object db = null;', content)
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/utils/GlobalMangaStatsManager.java', 'w') as f:
    f.write(content)

# CommentsActivity.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'r') as f:
    content = f.read()
content = re.sub(r'private FirebaseFirestore db;', 'private Object db;', content)
content = content.replace('db = FirebaseFirestore.getInstance();', 'db = null;')
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'Query query = .*?;', '', content)
content = re.sub(r'query\.get\(\).*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'w') as f:
    f.write(content)

# CommentsBottomSheetDialog.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()
content = re.sub(r'private FirebaseFirestore db;', 'private Object db;', content)
content = content.replace('db = FirebaseFirestore.getInstance();', 'db = null;')
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'Query query = .*?;', '', content)
content = re.sub(r'query\.get\(\).*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)

# CommentAdapter.java
with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()
content = re.sub(r'FirebaseFirestore db = FirebaseFirestore\.getInstance\(\);', 'Object db = null;', content)
content = re.sub(r'DocumentReference commentRef = .*?;', '', content)
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'commentRef\.update.*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'commentRef\.delete.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)

# AdminDashboardActivity.java
with open('app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java', 'r') as f:
    content = f.read()
content = re.sub(r'private DatabaseReference settingsRef;', 'private Object settingsRef;', content)
content = content.replace('settingsRef = FirebaseDatabase.getInstance().getReference("settings");', 'settingsRef = null;')
content = re.sub(r'settingsRef\.setValue.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java', 'w') as f:
    f.write(content)

# CommentRepository.java
with open('app/src/main/java/com/fire/mangareader/network/CommentRepository.java', 'r') as f:
    content = f.read()
content = re.sub(r'private final FirebaseDatabase database;', 'private final Object database;', content)
content = re.sub(r'private final DatabaseReference commentsRef;', 'private final Object commentsRef;', content)
content = content.replace('database = FirebaseDatabase.getInstance();', 'database = null;')
content = content.replace('commentsRef = database.getReference("comments");', 'commentsRef = null;')
content = re.sub(r'commentsRef\.child.*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'commentsRef\.orderByChild.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/network/CommentRepository.java', 'w') as f:
    f.write(content)
