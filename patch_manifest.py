import re

manifest_path = "app/src/main/AndroidManifest.xml"
with open(manifest_path, "r") as f:
    content = f.read()

content = content.replace('android:theme="@style/Theme.MangaFire"', 'android:theme="@style/Theme.AppCompat.NoActionBar"')

with open(manifest_path, "w") as f:
    f.write(content)

