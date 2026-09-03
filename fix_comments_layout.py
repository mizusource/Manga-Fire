with open("app/src/main/res/layout/activity_comments.xml", "r") as f:
    content = f.read()

replacement = """    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/commentsRecyclerView"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:clipToPadding="false"
        android:paddingTop="8dp" />

    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:layout_gravity="center"
        android:visibility="gone" />

    <TextView
        android:id="@+id/tvEmptyComments"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:gravity="center"
        android:text="لا توجد تعليقات بعد، كن أول من يعلق!"
        android:textColor="?attr/colorOnSurfaceVariant"
        android:visibility="gone" />
"""

import re
content = re.sub(r'<androidx\.recyclerview\.widget\.RecyclerView\s*android:id="@+id/commentsRecyclerView"\s*android:layout_width="match_parent"\s*android:layout_height="0dp"\s*android:layout_weight="1"\s*android:clipToPadding="false"\s*android:paddingTop="8dp"\s*/>', replacement, content)

with open("app/src/main/res/layout/activity_comments.xml", "w") as f:
    f.write(content)
