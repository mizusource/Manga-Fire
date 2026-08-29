import re

with open('app/src/main/res/layout/activity_main.xml', 'r') as f:
    content = f.read()

tabs_bar = """
            <!-- Search and Tabs Bar -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="52dp"
                android:layout_marginHorizontal="16dp"
                android:layout_marginVertical="8dp"
                android:background="@drawable/bg_glassmorphism_card"
                android:orientation="horizontal"
                android:paddingHorizontal="16dp"
                android:gravity="center_vertical">

                <ImageView
                    android:id="@+id/btnSearch"
                    android:layout_width="24dp"
                    android:layout_height="24dp"
                    android:src="@drawable/ic_search"
                    app:tint="#B3FFFFFF"
                    android:background="?attr/selectableItemBackgroundBorderless" />

                <Space
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1" />

                <TextView
                    android:id="@+id/tabPopular"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:paddingHorizontal="16dp"
                    android:paddingVertical="8dp"
                    android:text="الأكثر شعبية"
                    android:textColor="#B3FFFFFF"
                    android:textSize="14sp" />

                <TextView
                    android:id="@+id/tabLatest"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:paddingHorizontal="16dp"
                    android:paddingVertical="8dp"
                    android:text="آخر التحديثات"
                    android:textColor="#FFFFFF"
                    android:textSize="14sp"
                    android:textStyle="bold"
                    android:background="@drawable/bg_tab_active" />

                <ImageView
                    android:layout_width="24dp"
                    android:layout_height="24dp"
                    android:layout_marginStart="12dp"
                    android:src="@drawable/ic_filter"
                    app:tint="#B3FFFFFF"
                    android:background="?attr/selectableItemBackgroundBorderless" />

            </LinearLayout>
"""

# Replace the old Search and Tabs Bar
content = re.sub(r'<!-- Search and Tabs Bar -->[\s\S]*?</com\.google\.android\.material\.appbar\.AppBarLayout>', tabs_bar + '\n        </com.google.android.material.appbar.AppBarLayout>', content)

with open('app/src/main/res/layout/activity_main.xml', 'w') as f:
    f.write(content)
