import re

filepath = 'app/src/main/AndroidManifest.xml'
with open(filepath, 'r') as f:
    content = f.read()

receiver_xml = """        <receiver android:name=".data.service.NotificationReceiver" android:exported="false">
            <intent-filter>
                <action android:name="com.fire.mangareader.ACTION_CANCEL_DOWNLOAD" />
            </intent-filter>
        </receiver>
        
        <service
"""
content = content.replace('        <service', receiver_xml, 1)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched AndroidManifest.xml with NotificationReceiver")
