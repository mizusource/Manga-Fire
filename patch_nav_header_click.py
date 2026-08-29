import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

import_target = "import com.fire.mangareader.activity.ProfileActivity;"
import_replace = "import com.fire.mangareader.activity.ProfileActivity;\nimport com.fire.mangareader.activity.NotificationsActivity;"

if "NotificationsActivity" not in content:
    content = content.replace(import_target, import_replace)

nav_target = "btnEditProfile.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class)));"
nav_replace = """btnEditProfile.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class)));
            btnNotifications.setOnClickListener(v -> startActivity(new android.content.Intent(MainActivity.this, NotificationsActivity.class)));"""

content = content.replace(nav_target, nav_replace)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
