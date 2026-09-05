import re

with open("app/src/main/res/layout/activity_chapter_reader.xml", "r") as f:
    text = f.read()

# I will find the first occurrence of eyeFilterOverlay and remove everything from it onwards,
# then append exactly ONE copy of eyeFilterOverlay, fabSettings, and the closing </FrameLayout>

pattern = r'(\s*<!-- Color Filter Overlay.*)'
text = re.split(pattern, text)[0]

eye_filter = """
        <!-- Color Filter Overlay (حماية العين و الوضع الليلي) -->
        <View
            android:id="@+id/eyeFilterOverlay"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@android:color/transparent"
            android:elevation="100dp"
            android:clickable="false"
            android:focusable="false" />
            
        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/fabSettings"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom|end"
            android:layout_margin="24dp"
            android:src="@drawable/ic_drawer_settings"
            app:backgroundTint="#88000000"
            app:tint="@android:color/white"
            app:elevation="0dp" />
    </FrameLayout>
</FrameLayout>
"""

with open("app/src/main/res/layout/activity_chapter_reader.xml", "w") as f:
    f.write(text + eye_filter)

