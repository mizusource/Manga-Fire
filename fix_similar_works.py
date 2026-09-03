with open("app/src/main/res/layout/activity_manga_detail.xml", "r") as f:
    content = f.read()

import re
content = re.sub(
    r'(<TextView\s+android:layout_width="wrap_content"\s+android:layout_height="wrap_content"\s+android:layout_marginHorizontal="24dp"\s+android:text="أعمال مشابهة")',
    r'\1\n                            android:visibility="gone"',
    content
)

content = re.sub(
    r'(<androidx\.recyclerview\.widget\.RecyclerView\s+android:id="@+id/recommendationsRecyclerView")',
    r'\1\n                            android:visibility="gone"',
    content
)

with open("app/src/main/res/layout/activity_manga_detail.xml", "w") as f:
    f.write(content)
