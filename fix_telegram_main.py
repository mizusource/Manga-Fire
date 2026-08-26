with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "r") as f:
    content = f.read()

telegram_import = "import com.fire.mangareader.utils.TelegramManager;\n"
content = content.replace("import com.fire.mangareader.model.Manga;", "import com.fire.mangareader.model.Manga;\n" + telegram_import)

old_nav = """            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }"""

new_nav = """            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            } else if (id == R.id.nav_telegram) {
                TelegramManager.openTelegramChannel(MainActivity.this, "speedmanga");
            }"""

content = content.replace(old_nav, new_nav)

with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "w") as f:
    f.write(content)
