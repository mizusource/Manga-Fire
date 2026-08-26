with open("app/src/main/res/menu/drawer_menu.xml", "r") as f:
    content = f.read()

admin_item = """            <item
                android:id="@+id/nav_admin"
                android:icon="@android:drawable/ic_menu_manage"
                android:title="لوحة الإدارة" />"""

if "nav_admin" not in content:
    content = content.replace('android:id="@+id/nav_settings"', admin_item + '\n            <item\n                android:id="@+id/nav_settings"')
    with open("app/src/main/res/menu/drawer_menu.xml", "w") as f:
        f.write(content)
