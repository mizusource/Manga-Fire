with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()

old_post_dialog = '''        Comment newComment = new Comment();
        newComment.user_name = userName;
        newComment.username = userName;
        newComment.text = text;
        newComment.timestamp = System.currentTimeMillis();'''

new_post_dialog = '''        Comment newComment = new Comment();
        newComment.user_name = userName;
        newComment.username = userName;
        newComment.text = text;
        newComment.timestamp = System.currentTimeMillis();
        
        String savedPic = prefs.getProfilePic();
        if (savedPic != null && !savedPic.isEmpty()) {
            newComment.user_avatar = savedPic;
        } else if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getPhotoUrl() != null) {
            newComment.user_avatar = mAuth.getCurrentUser().getPhotoUrl().toString();
        }'''
content = content.replace(old_post_dialog, new_post_dialog)

with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)


with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'r') as f:
    content = f.read()

old_post_act = '''        Comment newComment = new Comment(mangaUrl, username, text, timestamp, isSpoiler);'''

new_post_act = '''        Comment newComment = new Comment(mangaUrl, username, text, timestamp, isSpoiler);
        com.fire.mangareader.utils.PreferenceManager prefs = new com.fire.mangareader.utils.PreferenceManager(this);
        String savedPic = prefs.getProfilePic();
        if (savedPic != null && !savedPic.isEmpty()) {
            newComment.user_avatar = savedPic;
        } else if (currentUser.getPhotoUrl() != null) {
            newComment.user_avatar = currentUser.getPhotoUrl().toString();
        }'''
content = content.replace(old_post_act, new_post_act)

with open('app/src/main/java/com/fire/mangareader/activity/CommentsActivity.java', 'w') as f:
    f.write(content)
