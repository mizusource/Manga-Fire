import re

filepath = 'app/src/main/res/layout/item_manga_grid.xml'
with open(filepath, 'r') as f:
    content = f.read()

# Change backgrounds/tints to use purple hints
content = content.replace('app:cardBackgroundColor="@android:color/transparent"', 'app:cardBackgroundColor="#1A9C27B0" app:strokeColor="#4D9C27B0" app:strokeWidth="1dp"')
content = content.replace('android:background="@drawable/gradient_bottom"', 'android:background="@drawable/gradient_bottom_purple"')

with open(filepath, 'w') as f:
    f.write(content)
print("Updated grid xml")
