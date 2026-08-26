with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "r") as f:
    content = f.read()

old_nav = """            } else if (id == R.id.nav_settings) {
                // الانتقال لشاشة الإعدادات
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }"""

new_nav = """            } else if (id == R.id.nav_settings) {
                // الانتقال لشاشة الإعدادات
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            } else if (id == R.id.nav_telegram) {
                com.fire.mangareader.utils.TelegramManager.openTelegramChannel(MainActivity.this, "speedmanga");
            }"""

content = content.replace(old_nav, new_nav)

with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "w") as f:
    f.write(content)
