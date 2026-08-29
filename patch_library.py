import re

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'r') as f:
    content = f.read()

fixed_filter = """
    private void filterList(int tabPosition) {
        displayList.clear();
        String targetStatus = "";
        if (tabPosition == 1) targetStatus = "reading";
        else if (tabPosition == 2) targetStatus = "plan_to_read";
        else if (tabPosition == 3) targetStatus = "completed";
        else if (tabPosition == 4) targetStatus = "favorite";

        for (Manga manga : allLibraryItems) {
            if (tabPosition == 0 || manga.getRating().equals(targetStatus)) {
                displayList.add(manga);
            }
        }

        // Fix the visual bug by restoring heart in adapter? 
        // No, we can just leave rating as status text, but user sees 'reading' etc in the top-right pill.
        // Actually, let's just use `manga.setRating("❤️")` inside the mapping and check `manga.getLatestChapter()` which holds the Arabic string.

"""

new_filterList = """    private void filterList(int tabPosition) {
        displayList.clear();
        String targetStatusAr = "";
        if (tabPosition == 1) targetStatusAr = "أقرأها حالياً";
        else if (tabPosition == 2) targetStatusAr = "سأقرأها";
        else if (tabPosition == 3) targetStatusAr = "مكتملة";
        else if (tabPosition == 4) targetStatusAr = "مفضلة";

        for (Manga manga : allLibraryItems) {
            if (tabPosition == 0 || (manga.getLatestChapter() != null && manga.getLatestChapter().equals(targetStatusAr))) {
                displayList.add(manga);
            }
        }

        adapter.notifyDataSetChanged();

        if (displayList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            rvLibrary.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            rvLibrary.setVisibility(View.VISIBLE);
        }
    }
"""

content = re.sub(r'private void filterList\(int tabPosition\).*?\}\s*\}', new_filterList.strip() + '\n}', content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/activity/LibraryActivity.java', 'w') as f:
    f.write(content)
