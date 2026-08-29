with open('app/src/main/res/layout/bottom_sheet_comments.xml', 'r') as f:
    content = f.read()

old_content = """    <!-- Comments List -->
    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rvComments"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:padding="16dp" />

    <TextView
        android:id="@+id/tvEmptyComments"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:gravity="center"
        android:text="لا توجد تعليقات حتى الآن.\\nكن أول من يشارك برأيه في هذا العمل!"
        android:textColor="?attr/colorOnSurfaceVariant"
        android:textSize="14sp"
        android:visibility="gone" />"""

new_content = """    <!-- Comments List Container -->
    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/rvComments"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:clipToPadding="false"
            android:padding="16dp" />

        <TextView
            android:id="@+id/tvEmptyComments"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:gravity="center"
            android:text="لا توجد تعليقات حتى الآن.\\nكن أول من يشارك برأيه في هذا العمل!"
            android:textColor="?attr/colorOnSurfaceVariant"
            android:textSize="14sp"
            android:visibility="gone" />
            
        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:visibility="gone" />
            
    </RelativeLayout>"""

if old_content in content:
    with open('app/src/main/res/layout/bottom_sheet_comments.xml', 'w') as f:
        f.write(content.replace(old_content, new_content))
    print("Patched bottom_sheet_comments.xml")
else:
    print("Could not find the target content in bottom_sheet_comments.xml")

