with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Find where chaptersRecycler is initialized
init_str = "chaptersRecycler = findViewById(R.id.chaptersRecyclerView);"

# Add pagination logic
pagination_code = """
        chaptersRecycler = findViewById(R.id.chaptersRecyclerView);
        
        // Fix ANR by lazy loading chapters inside NestedScrollView
        androidx.core.widget.NestedScrollView nestedScrollView = findViewById(R.id.nestedScrollView);
        if (nestedScrollView != null) {
            nestedScrollView.setOnScrollChangeListener(new androidx.core.widget.NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(androidx.core.widget.NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        if (chapterAdapter != null && chapterAdapter.getItemCount() < chapterList.size()) {
                            int currentSize = chapterAdapter.getItemCount();
                            int nextLimit = Math.min(currentSize + 50, chapterList.size());
                            chapterAdapter.setDisplayLimit(nextLimit);
                        }
                    }
                }
            });
        }
"""

content = content.replace(init_str, pagination_code.strip())

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
