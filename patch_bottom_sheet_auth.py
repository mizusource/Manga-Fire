import re

with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'r') as f:
    content = f.read()

# Imports
content = re.sub(r'import com.google.firebase.auth.FirebaseAuth;\n?', '', content)
content = re.sub(r'import com.google.firebase.auth.FirebaseUser;\n?', '', content)
content = re.sub(r'(package .*?;)', r'\1\nimport com.fire.mangareader.network.SupabaseManager;', content, count=1)

# Variables
content = re.sub(r'private FirebaseAuth mAuth;\n?', '', content)
content = re.sub(r'mAuth = FirebaseAuth\.getInstance\(\);\n?', '', content)

# checkAuthStatus
old_auth_check = """        FirebaseUser user = mAuth.getCurrentUser();
        PreferenceManager prefs = new PreferenceManager(requireContext());
        if (user == null || prefs.isGuest()) {"""
new_auth_check = """        PreferenceManager prefs = new PreferenceManager(requireContext());
        if (!SupabaseManager.getInstance(requireContext()).isLoggedIn() || prefs.isGuest()) {"""
content = content.replace(old_auth_check, new_auth_check)

# postComment photo url check
old_photo_check = "} else if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getPhotoUrl() != null) {"
new_photo_check = "} else if (false) { // Using Supabase, picture would come from prefs or Supabase profile."
content = content.replace(old_photo_check, new_photo_check)

old_photo_assign = "newComment.user_avatar = mAuth.getCurrentUser().getPhotoUrl().toString();"
new_photo_assign = ""
content = content.replace(old_photo_assign, new_photo_assign)

with open('app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java', 'w') as f:
    f.write(content)
