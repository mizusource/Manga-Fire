import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

if "xmlns:tools" not in content:
    content = content.replace('<manifest xmlns:android="http://schemas.android.com/apk/res/android">', 
                              '<manifest xmlns:android="http://schemas.android.com/apk/res/android"\n    xmlns:tools="http://schemas.android.com/tools">')

service_tag = """
        <service
            android:name="androidx.work.impl.foreground.SystemForegroundService"
            android:foregroundServiceType="dataSync"
            tools:node="merge" />
"""

if "SystemForegroundService" not in content:
    content = content.replace('</application>', service_tag + '</application>')

# Also remove duplicate POST_NOTIFICATIONS
content = content.replace('<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />', '', 1)

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)
print("Fixed AndroidManifest.xml")
