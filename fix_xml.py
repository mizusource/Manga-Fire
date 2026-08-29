with open('app/src/main/res/layout/activity_manga_detail.xml', 'r') as f:
    content = f.read()

content = content.replace('<androidx.core.widget.NestedScrollView', '<androidx.core.widget.NestedScrollView\n        android:id="@+id/nestedScrollView"')

with open('app/src/main/res/layout/activity_manga_detail.xml', 'w') as f:
    f.write(content)
