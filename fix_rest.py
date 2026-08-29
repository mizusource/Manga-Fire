import re
import os

def replace_all(path, changes):
    if not os.path.exists(path): return
    with open(path, 'r') as f:
        content = f.read()
    for old, new in changes:
        content = content.replace(old, new)
    with open(path, 'w') as f:
        f.write(content)

# MainActivity.java
replace_all('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', [
    ('com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();', 'Object user = null;'),
    ('if (user != null && user.getEmail() != null && user.getEmail().equals("mstfybdwy633@gmail.com"))', 'if (false)')
])

# AdminDashboardActivity.java
replace_all('app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java', [
    ('com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();', 'Object currentUser = null;'),
    ('DatabaseReference ref = FirebaseDatabase.getInstance().getReference("settings");', 'Object ref = null;')
])

# CommentsActivity.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'r') as f:
    content = f.read()
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'w') as f:
    f.write(content)

# CommentsBottomSheetDialog.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()
content = re.sub(r'db\.collection.*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)

# CommentAdapter.java
with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()
content = re.sub(r'DocumentReference ref = FirebaseFirestore.*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'FirebaseFirestore\.getInstance\(\)\.collection\("reports"\).*?\}\);', '', content, flags=re.DOTALL)
content = re.sub(r'FirebaseFirestore\.getInstance\(\).*?\}\);', '', content, flags=re.DOTALL)
with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)

