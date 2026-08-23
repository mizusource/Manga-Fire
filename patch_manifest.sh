sed -i '/<\/application>/i \
        <activity android:name=".activity.StorageManagerActivity" android:exported="false" \/>\n' app/src/main/AndroidManifest.xml
sed -i '/<uses-permission android:name="android.permission.INTERNET"/i \
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"\/>' app/src/main/AndroidManifest.xml
