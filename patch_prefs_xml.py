with open("app/src/main/res/xml/preferences.xml", "r") as f:
    content = f.read()

parser_cat = """
    <PreferenceCategory android:title="محرك البيانات (Parser Engine)">
        <Preference
            android:key="parser_sync_url"
            android:title="تحديث إعدادات المحرك"
            android:summary="جلب أحدث إعدادات تخطي الحجب ومسارات المواقع" />
    </PreferenceCategory>
"""

content = content.replace("</PreferenceScreen>", parser_cat + "</PreferenceScreen>")

with open("app/src/main/res/xml/preferences.xml", "w") as f:
    f.write(content)
print("Added Parser Settings to preferences.xml")
