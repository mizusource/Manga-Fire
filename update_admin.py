import os

manifest_path = 'app/src/main/AndroidManifest.xml'
with open(manifest_path, 'r') as f:
    manifest = f.read()

if '<activity android:name=".activity.AdminLoginActivity" />' not in manifest:
    manifest = manifest.replace(
        '<activity android:name=".activity.AdminDashboardActivity"  />',
        '<activity android:name=".activity.AdminDashboardActivity"  />\n        <activity android:name=".activity.AdminLoginActivity" />'
    )
    with open(manifest_path, 'w') as f:
        f.write(manifest)
    print("Patched AndroidManifest.xml")

main_path = 'app/src/main/java/com/fire/mangareader/activity/MainActivity.java'
with open(main_path, 'r') as f:
    main_code = f.read()

old_code = 'startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));'
new_code = 'startActivity(new Intent(MainActivity.this, AdminLoginActivity.class));'

if old_code in main_code:
    main_code = main_code.replace(old_code, new_code)
    with open(main_path, 'w') as f:
        f.write(main_code)
    print("Patched MainActivity.java")
