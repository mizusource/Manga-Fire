import re

xml_path = "app/src/main/res/layout/bottom_sheet_my_list.xml"
with open(xml_path, "r") as f:
    xml_content = f.read()

custom_list_xml = """
    <View
        android:layout_width="match_parent"
        android:layout_height="1dp"
        android:layout_marginVertical="8dp"
        android:background="?attr/colorSurfaceVariant" />

    <TextView
        android:id="@+id/statusCustomList"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="إضافة إلى قائمة مخصصة"
        android:textSize="18sp"
        android:paddingVertical="12dp"
        android:textColor="?attr/colorPrimary"
        android:background="?attr/selectableItemBackground"
        android:drawableEnd="@android:drawable/ic_menu_agenda" />
"""

xml_content = xml_content.replace('    <TextView\n        android:id="@+id/statusDropped"', custom_list_xml + '\n    <TextView\n        android:id="@+id/statusDropped"')

with open(xml_path, "w") as f:
    f.write(xml_content)

