import re

with open("app/src/main/res/layout/dialog_search_filter.xml", "r") as f:
    text = f.read()

# Replace FlexboxLayout with HorizontalScrollView -> LinearLayout for chips if Flexbox is not available
pattern = r'<com\.google\.android\.flexbox\.FlexboxLayout.*?</com\.google\.android\.flexbox\.FlexboxLayout>'

replacement = """
    <HorizontalScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:scrollbars="none"
        android:layout_marginBottom="24dp">
        <com.google.android.material.chip.ChipGroup
            android:id="@+id/chipGroupGenres"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:singleLine="true" />
    </HorizontalScrollView>
"""

new_text = re.sub(pattern, replacement, text, flags=re.DOTALL)

with open("app/src/main/res/layout/dialog_search_filter.xml", "w") as f:
    f.write(new_text)
