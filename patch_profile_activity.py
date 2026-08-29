import re

with open('app/src/main/java/com/fire/mangareader/activity/ProfileActivity.java', 'r') as f:
    content = f.read()

bottom_nav_logic = """
    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav == null) return;
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                return true;
            } else if (id == R.id.nav_home) {
                startActivity(new android.content.Intent(this, MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return false;
            } else if (id == R.id.nav_library) {
                startActivity(new android.content.Intent(this, LibraryActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return false;
            } else if (id == R.id.nav_downloads) {
                startActivity(new android.content.Intent(this, DownloadsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return false;
            }
            return false;
        });
    }
"""

if "setupBottomNavigation" not in content:
    # insert setupBottomNavigation after loadStats();
    content = content.replace('loadStats();\n    }', 'loadStats();\n        setupBottomNavigation();\n    }\n' + bottom_nav_logic)

with open('app/src/main/java/com/fire/mangareader/activity/ProfileActivity.java', 'w') as f:
    f.write(content)
