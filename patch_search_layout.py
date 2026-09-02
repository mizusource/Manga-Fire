import re

filepath = 'app/src/main/res/layout/activity_search.xml'
with open(filepath, 'r') as f:
    content = f.read()

target = """<com.google.android.material.chip.Chip
            android:id="@+id/chipGlobalSearch\""""

replacement = """<com.google.android.material.chip.Chip
            android:id="@+id/chipFilter"
            style="@style/Widget.MaterialComponents.Chip.Action"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="تصفية 🔍"
            android:layout_marginEnd="8dp"
            app:chipCornerRadius="16dp" />

        <com.google.android.material.chip.Chip
            android:id="@+id/chipGlobalSearch\""""

content = content.replace(target, replacement)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched activity_search.xml")
