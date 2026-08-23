sed -i 's/android:layout_toStartOf="@+id\/btnEyeFilter"/android:layout_toStartOf="@+id\/btnToggleDirection"/g' app/src/main/res/layout/activity_chapter_reader.xml
sed -i '/android:id="@+id\/btnEyeFilter"/i \
                    <ImageView \
                        android:id="@+id/btnToggleDirection" \
                        android:layout_width="48dp" \
                        android:layout_height="48dp" \
                        android:layout_toStartOf="@+id/btnEyeFilter" \
                        android:layout_centerVertical="true" \
                        android:background="?attr/selectableItemBackgroundBorderless" \
                        android:padding="12dp" \
                        android:src="@android:drawable/ic_menu_sort_by_size" \
                        app:tint="#FFFFFF" />' app/src/main/res/layout/activity_chapter_reader.xml
