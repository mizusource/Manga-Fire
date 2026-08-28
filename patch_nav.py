import re

# 1. Remove HorizontalScrollView from activity_main.xml
with open('app/src/main/res/layout/activity_main.xml', 'r') as f:
    main_content = f.read()

main_content = re.sub(r'<HorizontalScrollView.*?</HorizontalScrollView>', '', main_content, flags=re.DOTALL)

with open('app/src/main/res/layout/activity_main.xml', 'w') as f:
    f.write(main_content)

# 2. Modify nav_header.xml
nav_header = '''<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="?attr/colorSurface"
    android:gravity="center_vertical"
    android:orientation="horizontal"
    android:padding="24dp">
    <LinearLayout
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:orientation="vertical">
        <TextView
            android:id="@+id/navHeaderName"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="تسجيل الدخول"
            android:textColor="?attr/colorOnSurface"
            android:textSize="18sp"
            android:textStyle="bold" />
        <TextView
            android:id="@+id/navHeaderEmail"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="انقر هنا لتسجيل الدخول"
            android:textColor="?attr/colorOnSurfaceVariant"
            android:textSize="12sp"
            android:layout_marginTop="4dp" />
    </LinearLayout>
    
    <ImageView
        android:id="@+id/btnEditProfile"
        android:layout_width="32dp"
        android:layout_height="32dp"
        android:layout_marginEnd="12dp"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:padding="6dp"
        android:src="@android:drawable/ic_menu_edit"
        android:visibility="gone"
        app:tint="?attr/colorOnSurfaceVariant" />

    <ImageView
        android:id="@+id/navHeaderImage"
        android:layout_width="48dp"
        android:layout_height="48dp"
        android:background="@drawable/circle_bg_purple"
        android:padding="12dp"
        android:src="@drawable/ic_drawer_profile"
        app:tint="?attr/colorPrimary" />
</LinearLayout>'''

with open('app/src/main/res/layout/nav_header.xml', 'w') as f:
    f.write(nav_header)

