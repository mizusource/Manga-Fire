with open('app/src/main/res/layout/fragment_home.xml', 'r') as f:
    content = f.read()

content = content.replace('android:background="@color/background"', 'android:background="?android:attr/colorBackground"')

with open('app/src/main/res/layout/fragment_home.xml', 'w') as f:
    f.write(content)
