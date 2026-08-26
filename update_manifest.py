import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

# Add FOREGROUND_SERVICE permission
perm = '<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />\n    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />\n'
if "android.permission.FOREGROUND_SERVICE" not in content:
    content = content.replace('<application', perm + '<application')

# Add Service definition
svc = '\n        <service android:name=".service.DownloadService"\n            android:foregroundServiceType="dataSync"\n            android:exported="false" />\n'
if ".service.DownloadService" not in content:
    content = content.replace('</application>', svc + '    </application>')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
