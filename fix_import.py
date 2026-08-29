with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

import_str = 'import com.fire.mangareader.activity.AdminLoginActivity;'
if import_str not in content:
    content = content.replace(
        'import com.fire.mangareader.activity.AdminDashboardActivity;',
        'import com.fire.mangareader.activity.AdminDashboardActivity;\nimport com.fire.mangareader.activity.AdminLoginActivity;'
    )
    with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
        f.write(content)
    print("Added import to MainActivity")
