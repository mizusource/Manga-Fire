import re

with open('app/src/main/res/layout/activity_manga_detail.xml', 'r') as f:
    content = f.read()

# Let's completely remove the second SwipeRefreshLayout opening tag (around line 337)
# It might look like:
# <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
#     android:id="@+id/swipeRefreshLayout"
#     android:layout_width="match_parent"
#     android:layout_height="wrap_content"
#     android:visibility="gone">

content = re.sub(r'<androidx\.swiperefreshlayout\.widget\.SwipeRefreshLayout[^>]*visibility="gone">', '', content)

with open('app/src/main/res/layout/activity_manga_detail.xml', 'w') as f:
    f.write(content)
