with open("app/src/main/res/layout/activity_search.xml", "r") as f:
    content = f.read()

progress_bar = """        <ProgressBar
            android:id="@+id/progressBar"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:indeterminateTint="?attr/colorPrimary"
            android:visibility="gone" />"""

shimmer = """<include layout="@layout/layout_shimmer_mangas" 
            android:id="@+id/shimmerView" 
            android:visibility="gone" />"""
content = content.replace(progress_bar, shimmer)

with open("app/src/main/res/layout/activity_search.xml", "w") as f:
    f.write(content)
