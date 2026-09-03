with open("app/src/main/res/layout/activity_comments.xml", "r") as f:
    content = f.read()

target = """    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/commentsRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingTop="8dp" />"""
        
# Normalize whitespace to make matching easier
import re
content = re.sub(r'<androidx\.recyclerview\.widget\.RecyclerView.*?/>', 
'''    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/commentsRecyclerView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:clipToPadding="false"
            android:paddingTop="8dp" />

        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:visibility="gone" />

        <TextView
            android:id="@+id/tvEmptyComments"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:gravity="center"
            android:text="لا توجد تعليقات بعد، كن أول من يعلق!"
            android:textColor="?attr/colorOnSurfaceVariant"
            android:visibility="gone" />
    </FrameLayout>''', content, flags=re.DOTALL)

with open("app/src/main/res/layout/activity_comments.xml", "w") as f:
    f.write(content)
