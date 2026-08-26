import re

with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "r") as f:
    content = f.read()

# Add imports for Facebook Shimmer and LinearLayoutManager
imports = """
import com.facebook.shimmer.ShimmerFrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.widget.ImageView;
"""
content = content.replace("import androidx.recyclerview.widget.RecyclerView;", "import androidx.recyclerview.widget.RecyclerView;" + imports)

# Replace ProgressBar with ShimmerFrameLayout
content = content.replace("private ProgressBar mainProgressBar;", "private ShimmerFrameLayout mainShimmerView;\n    private ImageView btnToggleView;\n    private boolean isListView = false;")
content = content.replace("mainProgressBar = findViewById(R.id.mainProgressBar);", "mainShimmerView = findViewById(R.id.mainShimmerView);\n        btnToggleView = findViewById(R.id.btnToggleView);")

# Update setVisibility
content = content.replace("mainProgressBar.setVisibility(View.VISIBLE);", "mainShimmerView.setVisibility(View.VISIBLE);\n        mainShimmerView.startShimmer();")
content = content.replace("mainProgressBar.setVisibility(View.GONE);", "if(mainShimmerView != null) {\n                            mainShimmerView.stopShimmer();\n                            mainShimmerView.setVisibility(View.GONE);\n                        }")

# Add click listener for toggle
init_toggle = """        btnToggleView.setOnClickListener(v -> {
            isListView = !isListView;
            if (isListView) {
                btnToggleView.setImageResource(android.R.drawable.ic_menu_gallery);
                rvLatestUpdates.setLayoutManager(new LinearLayoutManager(this));
            } else {
                btnToggleView.setImageResource(android.R.drawable.ic_menu_sort_by_size);
                rvLatestUpdates.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 3));
            }
            adapter.setListView(isListView);
        });
"""
content = content.replace("rvLatestUpdates.setAdapter(adapter);", "rvLatestUpdates.setAdapter(adapter);\n" + init_toggle)

with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "w") as f:
    f.write(content)
