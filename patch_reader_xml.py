import re

with open("app/src/main/res/layout/activity_chapter_reader.xml", "r") as f:
    text = f.read()

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
            android:src="@drawable/ic_settings"
            app:backgroundTint="#88000000"
            app:tint="@android:color/white"
            app:elevation="0dp" />
"""

text = text.replace("</FrameLayout>", eye_filter + "\n    </FrameLayout>")

with open("app/src/main/res/layout/activity_chapter_reader.xml", "w") as f:
    f.write(text)
