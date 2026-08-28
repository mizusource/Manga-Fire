with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

content = content.replace('com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();',
'''android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);
            com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();''')

content = content.replace('android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);', '', 2) # remove the inner ones

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
