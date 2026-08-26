import re

with open("app/src/main/res/layout/activity_main.xml", "r") as f:
    content = f.read()

# Add btnToggleView next to btnSearch in Toolbar
toolbar_buttons = """                        <ImageView
                            android:id="@+id/btnToggleView"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_toStartOf="@id/btnSearch"
                            android:layout_centerVertical="true"
                            android:background="?attr/selectableItemBackgroundBorderless"
                            android:padding="12dp"
                            android:src="@android:drawable/ic_menu_agenda"
                            app:tint="?attr/colorOnSurface" />

                        <ImageView"""
content = content.replace("<ImageView", toolbar_buttons, 1) # Only first occurrence (no, let's be more specific)

with open("app/src/main/res/layout/activity_main.xml", "r") as f:
    content = f.read()

search_btn = """<ImageView
                            android:id="@+id/btnSearch" """

new_btns = """<ImageView
                            android:id="@+id/btnToggleView"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_toStartOf="@id/btnSearch"
                            android:layout_centerVertical="true"
                            android:background="?attr/selectableItemBackgroundBorderless"
                            android:padding="12dp"
                            android:src="@android:drawable/ic_menu_sort_by_size"
                            app:tint="?attr/colorOnSurface" />

                        <ImageView
                            android:id="@+id/btnSearch" """
content = content.replace(search_btn, new_btns)

progress_bar = """<ProgressBar
                    android:id="@+id/mainProgressBar"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center"
                    android:indeterminateTint="?attr/colorPrimary"
                    android:visibility="gone" />"""

shimmer = """<include layout="@layout/layout_shimmer_mangas" 
                    android:id="@+id/mainShimmerView" 
                    android:visibility="gone" />"""
content = content.replace(progress_bar, shimmer)

with open("app/src/main/res/layout/activity_main.xml", "w") as f:
    f.write(content)
