import re

with open("app/src/main/res/layout/activity_chapter_reader.xml", "r") as f:
    text = f.read()

# Change it back to View for eyeFilterOverlay specifically, and properly close the tags
text = text.replace('<com.fire.mangareader.presentation.reader.viewer.ReaderColorFilterView\n            android:id="@+id/eyeFilterOverlay"', '<View\n            android:id="@+id/eyeFilterOverlay"')

text = text.replace('<com.fire.mangareader.presentation.reader.viewer.ReaderColorFilterView\n            android:id="@+id/colorFilterView"\n            android:layout_width="1dp"\n            android:layout_height="1dp"\n            android:visibility="gone" />', '<com.fire.mangareader.presentation.reader.viewer.ReaderColorFilterView\n            android:id="@+id/colorFilterView"\n            android:layout_width="1dp"\n            android:layout_height="1dp"\n            android:visibility="gone" />')

with open("app/src/main/res/layout/activity_chapter_reader.xml", "w") as f:
    f.write(text)
