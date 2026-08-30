with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

if 'RepliesActivity' not in content:
    content = content.replace('<activity android:name=".activity.CommentsActivity" />',
                              '<activity android:name=".activity.CommentsActivity" />\n        <activity android:name=".activity.RepliesActivity" />')
    with open('app/src/main/AndroidManifest.xml', 'w') as f:
        f.write(content)
