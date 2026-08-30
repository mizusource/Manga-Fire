with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

import_str = 'import com.google.firebase.messaging.FirebaseMessaging;'
if import_str not in content:
    content = content.replace('import android.os.Bundle;', 'import android.os.Bundle;\n' + import_str)

fetch_token = """
        if (supabase.isLoggedIn()) {
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    supabase.updateFcmToken(task.getResult());
                }
            });
        }
"""

if 'FirebaseMessaging.getInstance().getToken()' not in content:
    # find onCreate end
    content = content.replace(
        'loadHomeData();',
        'loadHomeData();\n' + fetch_token
    )
    with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
        f.write(content)
    print("Added FirebaseMessaging token fetch")
