import re

def remove_tint(filepath, id_name):
    with open(filepath, 'r') as f:
        content = f.read()
    # Find the imageView block
    pattern = r'(<ImageView[^>]*android:id="@+id/' + id_name + r'"[^>]*)(app:tint="[^"]*")([^>]*/>)'
    content = re.sub(pattern, r'\1\3', content)
    with open(filepath, 'w') as f:
        f.write(content)

remove_tint('app/src/main/res/layout/fragment_profile.xml', 'profileImage')
remove_tint('app/src/main/res/layout/nav_header.xml', 'navHeaderImage')

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    main_java = f.read()

main_java = main_java.replace('''navHeaderImage.setImageResource(R.drawable.ic_drawer_profile);''',
'''navHeaderImage.setImageResource(R.drawable.ic_drawer_profile);
                    navHeaderImage.setColorFilter(getResources().getColor(R.color.colorPrimary, getTheme()));''')

main_java = main_java.replace('''Glide.with(this).load(android.net.Uri.parse(pm.getProfilePic())).circleCrop().into(navHeaderImage);''',
'''navHeaderImage.setColorFilter(null);
                    Glide.with(this).load(android.net.Uri.parse(pm.getProfilePic())).circleCrop().into(navHeaderImage);''')

main_java = main_java.replace('''Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(navHeaderImage);''',
'''navHeaderImage.setColorFilter(null);
                    Glide.with(this).load(user.getPhotoUrl()).circleCrop().into(navHeaderImage);''')

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(main_java)


with open('app/src/main/java/com/fire/mangareader/activity/ProfileActivity.java', 'r') as f:
    prof_java = f.read()

prof_java = prof_java.replace('''Glide.with(this).load(imageUri).circleCrop().into(profileImage);''',
'''profileImage.setColorFilter(null);
                            Glide.with(this).load(imageUri).circleCrop().into(profileImage);''')

prof_java = prof_java.replace('''Glide.with(this).load(Uri.parse(savedPic)).circleCrop().into(profileImage);''',
'''profileImage.setColorFilter(null);
            Glide.with(this).load(Uri.parse(savedPic)).circleCrop().into(profileImage);''')

with open('app/src/main/java/com/fire/mangareader/activity/ProfileActivity.java', 'w') as f:
    f.write(prof_java)

