with open("app/src/main/res/menu/drawer_menu.xml", "r") as f:
    content = f.read()

telegram_item = """
            <item
                android:id="@+id/nav_telegram"
                android:icon="@android:drawable/ic_menu_share"
                android:title="قناتنا على تيليجرام" />
        </menu>
"""

content = content.replace("</menu>", telegram_item + "\n    </item>\n</menu>", 1)

with open("app/src/main/res/menu/drawer_menu.xml", "w") as f:
    f.write(content)
