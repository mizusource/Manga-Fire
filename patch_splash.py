import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/SplashActivity.java", "r") as f:
    content = f.read()

# Add maintenance mode check
maintenance_code = '''
        com.fire.mangareader.util.AppAdminSettings.initialize(this);
        
        if (com.fire.mangareader.util.AppAdminSettings.maintenanceMode) {
            new AlertDialog.Builder(this)
                .setTitle("صيانة دورية")
                .setMessage(com.fire.mangareader.util.AppAdminSettings.maintenanceMessage)
                .setCancelable(false)
                .setPositiveButton("خروج", (dialog, which) -> finish())
                .show();
            return;
        }

        checkAppUpdate();
'''
content = content.replace("checkAppUpdate();", maintenance_code, 1)

# Also fix the login intent to just launch MainComposeActivity
intent_logic_old = '''            Intent intent;
            if (prefs.isFirstLaunch() || !prefs.isLoggedIn()) {
                intent = new Intent(this, LoginActivity.class);
            } else {
                intent = new Intent(this, MainComposeActivity.class);
            }'''
intent_logic_new = '''            Intent intent = new Intent(this, MainComposeActivity.class);'''
content = content.replace(intent_logic_old, intent_logic_new)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/SplashActivity.java", "w") as f:
    f.write(content)

