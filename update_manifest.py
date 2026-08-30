with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

service_xml = """
        <service
            android:name=".service.MyFirebaseMessagingService"
            android:exported="true">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
"""

if 'MyFirebaseMessagingService' not in content:
    content = content.replace(
        '</application>',
        service_xml + '\n    </application>'
    )
    with open('app/src/main/AndroidManifest.xml', 'w') as f:
        f.write(content)
    print("Added service to AndroidManifest.xml")
