import re

with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

# Remove intent-filter from MainComposeActivity/MainActivity (wherever it is)
content = re.sub(
    r'<activity\s+android:name="\.presentation\.activity\.MainComposeActivity"\s+android:exported="true">\s*<intent-filter>\s*<action android:name="android\.intent\.action\.MAIN"\s*/>\s*<category android:name="android\.intent\.category\.LAUNCHER"\s*/>\s*</intent-filter>\s*</activity>',
    '<activity android:name=".presentation.activity.MainActivity" android:exported="false" />',
    content
)

# Add intent-filter to SplashActivity
splash_old = r'<activity\s+android:name="\.presentation\.activity\.SplashActivity"\s+android:exported="false"\s+android:theme="@style/Theme\.MangaFire\.Splash"\s*/>'
splash_new = '''<activity
            android:name=".presentation.activity.SplashActivity"
            android:exported="true"
            android:theme="@style/Theme.MangaFire.Splash">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>'''
content = re.sub(splash_old, splash_new, content)

with open('app/src/main/AndroidManifest.xml', 'w') as f:
    f.write(content)
