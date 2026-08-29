import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

import_target = "import android.os.Bundle;"
import_replace = """import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;"""

if "import android.Manifest;" not in content:
    content = content.replace(import_target, import_replace)

init_target = "super.onCreate(savedInstanceState);"
init_replace = """super.onCreate(savedInstanceState);
        
        // Request Notification Permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
"""

if "Manifest.permission.POST_NOTIFICATIONS" not in content:
    content = content.replace(init_target, init_replace)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)

