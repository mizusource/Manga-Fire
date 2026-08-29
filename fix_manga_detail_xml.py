import re

with open('app/src/main/res/layout/activity_manga_detail.xml', 'r') as f:
    content = f.read()

# Let's fix missing closing tag for SwipeRefreshLayout.
# The error said: The element type "androidx.swiperefreshlayout.widget.SwipeRefreshLayout" must be terminated by the matching end-tag "</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>".
# Because I inserted the wrapper around NestedScrollView, but maybe there's another closing tag issue or it got stripped.
# Actually, I'll just check where SwipeRefreshLayout is opened and closed.

if '</androidx.swiperefreshlayout.widget.SwipeRefreshLayout>' not in content:
    # We should add it after NestedScrollView is closed
    content = content.replace('</androidx.core.widget.NestedScrollView>', '</androidx.core.widget.NestedScrollView>\n    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>')

with open('app/src/main/res/layout/activity_manga_detail.xml', 'w') as f:
    f.write(content)
