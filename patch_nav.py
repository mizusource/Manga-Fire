import re
with open('app/src/main/java/com/fire/mangareader/presentation/activity/MainActivity.java', 'r') as f:
    content = f.read()

old_nav = '''    private void setupNavigation() {
        if (navigationView == null) return;
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already here
            } else if (id == R.id.nav_downloads) {
                startActivity(new Intent(this, DownloadsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            }
            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });
    }'''

new_nav = '''    private void setupNavigation() {
        if (navigationView == null) return;
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                // Already here
            } else if (id == R.id.nav_currently_reading || id == R.id.nav_want_to_read || id == R.id.nav_completed || id == R.id.nav_favorites) {
                Intent intent = new Intent(this, LibraryActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_downloads) {
                startActivity(new Intent(this, DownloadsActivity.class));
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
            } else if (id == R.id.nav_admin) {
                startActivity(new Intent(this, AdminDashboardActivity.class));
            }
            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });
    }'''
content = content.replace(old_nav, new_nav)
with open('app/src/main/java/com/fire/mangareader/presentation/activity/MainActivity.java', 'w') as f:
    f.write(content)
