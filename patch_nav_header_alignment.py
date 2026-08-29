import re

with open('app/src/main/res/layout/nav_header.xml', 'r') as f:
    content = f.read()

# Swap End and Start
content = content.replace('alignParentStart="true"', 'alignParentStartTEMP')
content = content.replace('alignParentEnd="true"', 'alignParentStart="true"')
content = content.replace('alignParentStartTEMP', 'alignParentEnd="true"')

# For btnEditProfile layout_toEndOf, make it layout_toStartOf
content = content.replace('layout_toEndOf="@id/btnNotifications"', 'layout_toStartOf="@id/btnNotifications"')
content = content.replace('layout_marginStart="16dp"', 'layout_marginEnd="16dp"')

with open('app/src/main/res/layout/nav_header.xml', 'w') as f:
    f.write(content)
