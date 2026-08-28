with open('app/src/main/java/com/fire/mangareader/utils/ThemeHelper.java', 'r') as f:
    content = f.read()

new_logic = '''        String theme = prefs.getString("app_theme", "dark_neon_blue");

        if (theme.startsWith("classic_") && !theme.equals("classic_navy")) {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
        }

        switch (theme) {'''

content = content.replace('        String theme = prefs.getString("app_theme", "dark_neon_blue");\n        switch (theme) {', new_logic)

with open('app/src/main/java/com/fire/mangareader/utils/ThemeHelper.java', 'w') as f:
    f.write(content)
