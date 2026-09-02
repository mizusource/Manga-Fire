import re

filepath = 'app/src/main/res/layout/activity_settings.xml'
content = """<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?android:attr/colorBackground"
    android:orientation="vertical">

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="?attr/actionBarSize"
        android:paddingHorizontal="8dp"
        android:layout_marginTop="8dp">

        <ImageView
            android:id="@+id/btnBackSettings"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentStart="true"
            android:layout_centerVertical="true"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:padding="12dp"
            android:src="@drawable/ic_back_arrow"
            app:tint="?attr/colorOnBackground" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:text="@string/settings"
            android:textColor="?attr/colorOnBackground"
            android:textSize="20sp"
            android:textStyle="bold" />
    </RelativeLayout>

    <FrameLayout
        android:id="@+id/settings_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_margin="16dp"
        android:background="@drawable/bg_glassmorphism_card" />
</LinearLayout>
"""

with open(filepath, 'w') as f:
    f.write(content)
print("Updated activity_settings.xml")
