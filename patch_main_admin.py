with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "r") as f:
    content = f.read()

old_nav = """            } else if (id == R.id.nav_settings) {"""
new_nav = """            } else if (id == R.id.nav_admin) {
                startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
            } else if (id == R.id.nav_settings) {"""

if "nav_admin" not in content:
    content = content.replace(old_nav, new_nav)
    with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "w") as f:
        f.write(content)
