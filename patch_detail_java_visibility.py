import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

content = content.replace('swipeRefreshLayout.setVisibility(View.GONE);', 'chaptersRecyclerView.setVisibility(View.GONE);')
content = content.replace('swipeRefreshLayout.setVisibility(View.VISIBLE);', 'chaptersRecyclerView.setVisibility(View.VISIBLE);')

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
