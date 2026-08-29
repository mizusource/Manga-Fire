import re

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'r') as f:
    content = f.read()

bottom_nav_logic = """
    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;
        bottomNav.setSelectedItemId(R.id.nav_library);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_library) {
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new android.content.Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return false;
            } else if (id == R.id.nav_downloads) {
                startActivity(new android.content.Intent(this, DownloadsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return false;
            } else if (id == R.id.nav_profile) {
                startActivity(new android.content.Intent(this, ProfileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return false;
            }
            return false;
        });
    }
"""

if "setupBottomNavigation" not in content:
    # insert setupBottomNavigation after setContentView
    content = content.replace('setContentView(R.layout.activity_library);', 'setContentView(R.layout.activity_library);\n        setupBottomNavigation();')
    # add the method before the last brace
    last_brace = content.rfind('}')
    content = content[:last_brace] + bottom_nav_logic + content[last_brace:]

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'w') as f:
    f.write(content)
