import re

filepath = 'app/src/main/res/xml/preferences.xml'
with open(filepath, 'r') as f:
    content = f.read()

target = """    <PreferenceCategory android:title="@string/account">"""

replacement = """    <PreferenceCategory android:title="الإشعارات">
        <SwitchPreferenceCompat
            android:key="notifications_enabled"
            android:title="إشعارات الفصول الجديدة"
            android:summary="تلقي تنبيهات عند نزول فصول جديدة للمانجا المفضلة"
            android:defaultValue="false" />
    </PreferenceCategory>
    <PreferenceCategory android:title="@string/account">"""

content = content.replace(target, replacement)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched preferences.xml")
