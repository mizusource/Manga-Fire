import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

# Make sure we don't crash on mainToolbar if it was left somewhere
content = re.sub(r'Toolbar mainToolbar = findViewById\(R\.id\.mainToolbar\);', r'// Toolbar mainToolbar = findViewById(R.id.mainToolbar);', content)
content = re.sub(r'setSupportActionBar\(mainToolbar\);', r'// setSupportActionBar(mainToolbar);', content)

# Check toggle
content = re.sub(r'ActionBarDrawerToggle toggle = new ActionBarDrawerToggle\(this, drawerLayout, mainToolbar, R\.string\.navigation_drawer_open, R\.string\.navigation_drawer_close\);',
                 r'ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);', content)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
