import re

with open('app/src/main/res/layout/bottom_sheet_comments.xml', 'r') as f:
    content = f.read()

content = content.replace('android:background="?attr/colorSurface"', 'android:background="@drawable/bg_bottom_sheet"')

# Let's fix etCommentInput corner radius manually
content = content.replace(
    'android:background="?attr/colorSurface"',
    'android:background="@drawable/pill_bg_surface"'
)

with open('app/src/main/res/layout/bottom_sheet_comments.xml', 'w') as f:
    f.write(content)

with open('app/src/main/res/layout/bottom_sheet_my_list.xml', 'r') as f:
    content = f.read()

content = content.replace('android:background="?attr/colorSurface"', 'android:background="@drawable/bg_bottom_sheet"')

with open('app/src/main/res/layout/bottom_sheet_my_list.xml', 'w') as f:
    f.write(content)

