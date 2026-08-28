import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    main_content = f.read()

# Fix Glide replacement mess
main_content = main_content.replace('com.bumptech.glide.navHeaderImage.setColorFilter(null);\n                    Glide.with', 'navHeaderImage.setColorFilter(null);\n                    com.bumptech.glide.Glide.with')
main_content = main_content.replace('com.bumptech.glide.navHeaderImage.setColorFilter(null);\n                    Glide.with', 'navHeaderImage.setColorFilter(null);\n                    com.bumptech.glide.Glide.with')

# Fix colorPrimary issue
main_content = main_content.replace('getResources().getColor(R.color.colorPrimary, getTheme())', 'android.graphics.Color.GRAY')

# Fix btnEditProfile definition
main_content = main_content.replace('android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);', 'android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);', 1)
main_content = main_content.replace('android.widget.ImageView btnEditProfile = headerView.findViewById(R.id.btnEditProfile);', 'btnEditProfile = headerView.findViewById(R.id.btnEditProfile);') # second occurrence

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(main_content)
