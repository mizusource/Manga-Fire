    private void setupRecentReading() {
        android.view.View container = findViewById(R.id.recentReadingContainer);
        androidx.recyclerview.widget.RecyclerView rvRecent = findViewById(R.id.rvRecentReading);
        java.util.List<com.fire.mangareader.utils.RecentReadingManager.RecentItem> recentItems = com.fire.mangareader.utils.RecentReadingManager.getRecent(this);
        
        if (recentItems != null && !recentItems.isEmpty()) {
            container.setVisibility(android.view.View.VISIBLE);
            rvRecent.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            rvRecent.setAdapter(new com.fire.mangareader.adapter.RecentReadingAdapter(this, recentItems));
        } else {
            container.setVisibility(android.view.View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupRecentReading();
    }
