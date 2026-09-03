import re

filepath = 'app/src/main/AndroidManifest.xml'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('<activity android:name=".presentation.activity.MainActivity" />', 
                          '<activity android:name=".presentation.activity.MainComposeActivity" />\n        <activity android:name=".presentation.activity.CloudflareBypassActivity" />')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched AndroidManifest.xml")
