import re
with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

act = '        <activity android:name=".activity.NotificationsActivity" />'
if "NotificationsActivity" not in content:
    content = content.replace('</application>', act + '\n    </application>')

with open('app/src/main/AndroidManifest.xml', 'w') as f:
    f.write(content)
