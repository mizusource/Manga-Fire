import os

# 1. Fix item_hero_banner.xml layout_height
xml_path = 'app/src/main/res/layout/item_hero_banner.xml'
with open(xml_path, 'r') as f:
    xml_content = f.read()

old_xml = """    android:layout_width="match_parent"
    android:layout_height="200dp"
    android:layout_marginHorizontal="16dp"
    android:layout_marginVertical="8dp"
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp">"""

new_xml = """    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_marginHorizontal="16dp"
    android:layout_marginVertical="8dp"
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp">"""

if old_xml in xml_content:
    xml_content = xml_content.replace(old_xml, new_xml)
    with open(xml_path, 'w') as f:
        f.write(xml_content)
    print("Fixed item_hero_banner.xml")
else:
    print("Could not find TargetContent in item_hero_banner.xml")
    
# 2. Fix CommentsBottomSheetDialog.java ImageButton cast
java_path = 'app/src/main/java/com/fire/mangareader/activity/CommentsBottomSheetDialog.java'
with open(java_path, 'r') as f:
    java_content = f.read()

java_content = java_content.replace("import android.widget.ImageButton;", "import android.widget.ImageView;")
java_content = java_content.replace("private ImageButton btnSendComment;", "private ImageView btnSendComment;")

with open(java_path, 'w') as f:
    f.write(java_content)
print("Fixed CommentsBottomSheetDialog.java")

