import re

with open('app/src/main/res/layout/activity_library.xml', 'r') as f:
    content = f.read()

tab_layout = """
    <com.google.android.material.tabs.TabLayout
        android:id="@+id/tabLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="?attr/colorSurface"
        app:tabMode="scrollable"
        app:tabGravity="start"
        app:tabIndicatorColor="?attr/colorPrimary"
        app:tabSelectedTextColor="?attr/colorPrimary"
        app:tabTextColor="?attr/colorOnSurfaceVariant" />
"""

content = content.replace('</com.google.android.material.appbar.AppBarLayout>', '</com.google.android.material.appbar.AppBarLayout>\n' + tab_layout)

with open('app/src/main/res/layout/activity_library.xml', 'w') as f:
    f.write(content)

