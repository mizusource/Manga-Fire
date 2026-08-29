import os
import re

java_files = []
for root, dirs, files in os.walk('app/src/main/java'):
    for file in files:
        if file.endswith('.java'):
            java_files.append(os.path.join(root, file))

for path in java_files:
    with open(path, 'r') as f:
        content = f.read()
    
    modified = False
    
    # Imports
    if 'import com.google.firebase.auth.FirebaseAuth;' in content or 'import com.google.firebase.auth.FirebaseUser;' in content:
        content = re.sub(r'import com.google.firebase.auth.FirebaseAuth;\n?', '', content)
        content = re.sub(r'import com.google.firebase.auth.FirebaseUser;\n?', '', content)
        if 'import com.fire.mangareader.network.SupabaseManager;' not in content:
            # add import at the top after package
            content = re.sub(r'(package .*?;)', r'\1\nimport com.fire.mangareader.network.SupabaseManager;', content, count=1)
        modified = True
        
    # Replacements
    # AdminDashboardActivity.java:28:        com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
    if 'com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser()' in content:
        content = content.replace('com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null', 'SupabaseManager.getInstance(this).isLoggedIn()')
        content = content.replace('com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() == null', '!SupabaseManager.getInstance(this).isLoggedIn()')
        # What if it assigns to a variable?
        # We will handle AdminDashboardActivity manually if needed, or just replace the condition.
        modified = True

    # Generic FirebaseAuth.getInstance().getCurrentUser()
    if 'FirebaseAuth.getInstance().getCurrentUser()' in content:
        content = content.replace('FirebaseAuth.getInstance().getCurrentUser() != null', 'SupabaseManager.getInstance(this).isLoggedIn()')
        content = content.replace('FirebaseAuth.getInstance().getCurrentUser() == null', '!SupabaseManager.getInstance(this).isLoggedIn()')
        modified = True
        
    if 'FirebaseAuth.getInstance().signOut()' in content:
        content = content.replace('FirebaseAuth.getInstance().signOut()', 'SupabaseManager.getInstance(this).signOut()')
        modified = True
        
    if modified:
        with open(path, 'w') as f:
            f.write(content)

