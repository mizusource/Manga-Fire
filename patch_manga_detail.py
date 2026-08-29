import re

with open('app/src/main/res/layout/activity_manga_detail.xml', 'r') as f:
    content = f.read()

# Change root to have fitsSystemWindows
content = content.replace('xmlns:app="http://schemas.android.com/apk/res-auto"', 'xmlns:app="http://schemas.android.com/apk/res-auto"\n    android:fitsSystemWindows="true"')

# Remove padding top 32dp from floating toolbar to rely on fitsSystemWindows
content = content.replace('android:paddingTop="32dp"> <!-- For status bar if transparent -->', 'android:paddingTop="16dp">')

# Wrap NestedScrollView with SwipeRefreshLayout
nested_scroll_pattern = re.compile(r'<androidx\.core\.widget\.NestedScrollView.*?</androidx\.core\.widget\.NestedScrollView>', re.DOTALL)
nested_scroll_match = nested_scroll_pattern.search(content)

if nested_scroll_match:
    ns_str = nested_scroll_match.group(0)
    
    # Remove SwipeRefreshLayout from around RecyclerView
    ns_str = re.sub(r'<androidx\.swiperefreshlayout\.widget\.SwipeRefreshLayout[^>]*id="@+id/swipeRefreshLayout"[^>]*>', '', ns_str)
    ns_str = ns_str.replace('</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>', '')
    
    # The RecyclerView visibility was handled via swipeRefreshLayout in Java? Wait, SwipeRefreshLayout was visibility="gone".
    # I'll just set chaptersRecyclerView visibility="gone".
    ns_str = ns_str.replace('android:id="@+id/chaptersRecyclerView"', 'android:id="@+id/chaptersRecyclerView"\n                                android:visibility="gone"')
    
    wrapped_ns = f'''<androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        android:id="@+id/swipeRefreshLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
        {ns_str}
    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>'''
    
    content = content[:nested_scroll_match.start()] + wrapped_ns + content[nested_scroll_match.end():]

with open('app/src/main/res/layout/activity_manga_detail.xml', 'w') as f:
    f.write(content)
