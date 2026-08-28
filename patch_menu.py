with open('app/src/main/res/menu/drawer_menu.xml', 'r') as f:
    content = f.read()

content = content.replace('''        <item
            android:id="@+id/nav_profile"
            android:icon="@drawable/ic_drawer_profile"
            android:title="@string/profile" />''', 
'''        <item
            android:id="@+id/nav_favorites"
            android:icon="@android:drawable/btn_star"
            android:title="المفضلة" />''')

with open('app/src/main/res/menu/drawer_menu.xml', 'w') as f:
    f.write(content)
