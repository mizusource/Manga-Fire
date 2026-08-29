import re

with open('app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java', 'r') as f:
    content = f.read()

content = content.replace(
    'getSupportActionBar().setDisplayHomeAsUpEnabled(true);',
    'getSupportActionBar().setDisplayHomeAsUpEnabled(true);\n            getSupportActionBar().setDisplayShowTitleEnabled(false);'
)

with open('app/src/main/java/com/fire/mangareader/activity/AdminDashboardActivity.java', 'w') as f:
    f.write(content)
