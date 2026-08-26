import re

with open("app/src/main/res/layout/activity_search.xml", "r") as f:
    content = f.read()

progress_bar = """<ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_centerInParent="true"
            android:visibility="gone"
            android:indeterminateTint="?attr/colorPrimary" />"""

shimmer = """<include layout="@layout/layout_shimmer_mangas" 
            android:id="@+id/shimmerView" 
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_below="@id/chipGroup"
            android:layout_marginTop="8dp"
            android:visibility="gone" />"""
content = content.replace(progress_bar, shimmer)

with open("app/src/main/res/layout/activity_search.xml", "w") as f:
    f.write(content)


with open("app/src/main/java/com/fire/mangareader/activity/SearchActivity.java", "r") as f:
    content = f.read()

imports = """
import com.facebook.shimmer.ShimmerFrameLayout;
"""
content = content.replace("import android.widget.ProgressBar;", "import android.widget.ProgressBar;\n" + imports)
content = content.replace("private ProgressBar progressBar;", "private ShimmerFrameLayout shimmerView;")
content = content.replace("progressBar = findViewById(R.id.progressBar);", "shimmerView = findViewById(R.id.shimmerView);")
content = content.replace("progressBar.setVisibility(View.VISIBLE);", "shimmerView.setVisibility(View.VISIBLE);\n        shimmerView.startShimmer();")
content = content.replace("progressBar.setVisibility(View.GONE);", "shimmerView.stopShimmer();\n                    shimmerView.setVisibility(View.GONE);")

with open("app/src/main/java/com/fire/mangareader/activity/SearchActivity.java", "w") as f:
    f.write(content)
