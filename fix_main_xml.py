with open("app/src/main/res/layout/activity_main.xml", "r") as f:
    content = f.read()

search_btn = """                        <ImageView
                            android:id="@+id/btnSearch\""""

new_btns = """                        <ImageView
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
                            android:id="@+id/btnSearch\""""
content = content.replace(search_btn, new_btns)

with open("app/src/main/res/layout/activity_main.xml", "w") as f:
    f.write(content)
