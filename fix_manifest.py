with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

activity = '        <activity android:name=".activity.AdminDashboardActivity" android:theme="@style/Theme.MangaReader.NoActionBar" />'

if "AdminDashboardActivity" not in content:
    content = content.replace("</application>", activity + "\n    </application>")
    with open("app/src/main/AndroidManifest.xml", "w") as f:
        f.write(content)
