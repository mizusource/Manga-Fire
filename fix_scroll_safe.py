with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

old_scroll = """                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        if (chapterAdapter != null && chapterAdapter.getItemCount() < chapterList.size()) {"""

new_scroll = """                    if (v.getChildAt(0) != null && scrollY >= (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight() - 200)) {
                        if (chapterAdapter != null && chapterList != null && chapterAdapter.getItemCount() < chapterList.size()) {"""

if old_scroll in content:
    content = content.replace(old_scroll, new_scroll)
    with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
        f.write(content)
    print("Fixed scroll")
else:
    print("Not found")

