sed -i 's/<PreferenceCategory android:title="@string\/maintenance">/<PreferenceCategory android:title="Storage Management">/g' app/src/main/res/xml/preferences.xml
sed -i '/<Preference/,/clear_downloads_summary" \/>/c \
        <Preference\n            android:key="storage_manager"\n            android:title="Storage Manager"\n            android:summary="Manage cache, downloads, and auto-delete settings" \/>\n' app/src/main/res/xml/preferences.xml
