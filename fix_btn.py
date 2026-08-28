with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

content = content.replace('btnEditProfile = headerView.findViewById(R.id.btnEditProfile);', 'android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);')

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
