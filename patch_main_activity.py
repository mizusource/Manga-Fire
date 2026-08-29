import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

# Replace the intent calls with overridePendingTransition(0, 0)
content = content.replace(
    'startActivity(new android.content.Intent(MainActivity.this, LibraryActivity.class));\n                return false;',
    'startActivity(new android.content.Intent(MainActivity.this, LibraryActivity.class));\n                overridePendingTransition(0, 0);\n                return false;'
)
content = content.replace(
    'startActivity(new android.content.Intent(MainActivity.this, DownloadsActivity.class));\n                return false;',
    'startActivity(new android.content.Intent(MainActivity.this, DownloadsActivity.class));\n                overridePendingTransition(0, 0);\n                return false;'
)
content = content.replace(
    'startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class));\n                return false;',
    'startActivity(new android.content.Intent(MainActivity.this, ProfileActivity.class));\n                overridePendingTransition(0, 0);\n                return false;'
)

# Also make sure to select home by default if it's not already
if "bottomNav.setSelectedItemId(R.id.nav_home);" not in content:
    content = content.replace('if (bottomNav == null) return;', 'if (bottomNav == null) return;\n        bottomNav.setSelectedItemId(R.id.nav_home);')

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
