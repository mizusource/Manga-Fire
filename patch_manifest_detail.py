import re

manifest_path = "app/src/main/AndroidManifest.xml"
with open(manifest_path, "r") as f:
    content = f.read()

activity_tag = """
        <activity
            android:name=".presentation.activity.CustomListDetailActivity"
            android:exported="false"
            android:screenOrientation="portrait"
            android:theme="@style/Theme.AppCompat.NoActionBar" />
"""

if "CustomListDetailActivity" not in content:
    content = content.replace("</application>", activity_tag + "\n    </application>")

with open(manifest_path, "w") as f:
    f.write(content)

