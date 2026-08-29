import re

# CommentsActivity.java
with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'r') as f:
    content = f.read()

# I replaced `FirebaseUser currentUser = ...` with `boolean isLoggedIn = ...`
# Let's fix the usages:
content = content.replace('currentUser == null', '!isLoggedIn')
content = content.replace('currentUser.getDisplayName()', 'com.fire.mangareader.utils.PreferenceManager.getInstance(this).getUserName()')
content = content.replace('com.fire.mangareader.utils.PreferenceManager.getInstance(this).getUserName() != null', 'true')
content = content.replace('!com.fire.mangareader.utils.PreferenceManager.getInstance(this).getUserName().isEmpty()', 'true')
# Wait, let's just manually replace the username fetching block in CommentsActivity:
# String username = ...
username_block_old = """String username = true && true
                    ? com.fire.mangareader.utils.PreferenceManager.getInstance(this).getUserName() : "User";"""
username_block_new = """String username = new com.fire.mangareader.utils.PreferenceManager(this).getUserName();"""
content = content.replace(username_block_old, username_block_new)

# photoUrl
content = content.replace('} else if (currentUser.getPhotoUrl() != null) {', '} else if (false) {')
content = content.replace('newComment.user_avatar = currentUser.getPhotoUrl().toString();', '')

with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'w') as f:
    f.write(content)

# CommentAdapter.java
with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'r') as f:
    content = f.read()

content = content.replace('user != null', 'isLoggedIn')
content = content.replace('user == null', '!isLoggedIn')
content = content.replace('user.getUid()', 'com.fire.mangareader.network.SupabaseManager.getInstance(context).getCurrentUserId()')
content = content.replace('user.getEmail()', 'new com.fire.mangareader.utils.PreferenceManager(context).getUserEmail()')

with open('app/src/main/java/com/fire/mangareader/adapter/CommentAdapter.java', 'w') as f:
    f.write(content)

