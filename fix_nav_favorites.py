with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

old_nav = '''            } else if (id == R.id.nav_downloads) {
                // الانتقال لشاشة التنزيلات
                startActivity(new Intent(MainActivity.this, DownloadsActivity.class));
            } else if (id == R.id.nav_profile) {'''

new_nav = '''            } else if (id == R.id.nav_downloads) {
                // الانتقال لشاشة التنزيلات
                startActivity(new Intent(MainActivity.this, DownloadsActivity.class));
            } else if (id == R.id.nav_favorites) {
                // الانتقال للمفضلة
                startActivity(new Intent(MainActivity.this, LibraryActivity.class));
            } else if (id == R.id.nav_profile) {'''
content = content.replace(old_nav, new_nav)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
