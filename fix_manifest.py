import re

with open("app/src/main/AndroidManifest.xml", "r") as f:
    content = f.read()

# Remove MAIN/LAUNCHER from SplashActivity
content = content.replace('''        <activity
            android:name=".presentation.activity.SplashActivity"
            android:exported="true"
            android:theme="@style/Theme.MangaFire.Splash">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>''', '''        <activity
            android:name=".presentation.activity.SplashActivity"
            android:exported="false"
            android:theme="@style/Theme.MangaFire.Splash" />''')

# Add MAIN/LAUNCHER to MainComposeActivity
content = content.replace('''        <activity android:name=".presentation.activity.MainComposeActivity" />''', '''        <activity
            android:name=".presentation.activity.MainComposeActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>''')

with open("app/src/main/AndroidManifest.xml", "w") as f:
    f.write(content)

