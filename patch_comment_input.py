import re

filepath = 'app/src/main/res/layout/bottom_sheet_comments.xml'
with open(filepath, 'r') as f:
    content = f.read()

target = """    <!-- Input Area -->
    <LinearLayout
        android:id="@+id/layoutInput\""""

replacement = """    <!-- Input Area -->
    <LinearLayout
        android:id="@+id/layoutInput"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="?attr/colorSurfaceVariant"
        android:elevation="8dp"
        android:orientation="vertical"
        android:padding="12dp">
        
        <CheckBox
            android:id="@+id/cbSpoiler"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="التعليق يحتوي على حرق 🚨"
            android:textColor="?attr/colorOnSurfaceVariant"
            android:textSize="12sp"
            android:layout_marginBottom="4dp" />
            
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="horizontal"
            android:gravity="center_vertical">"""

content = content.replace(target, replacement)

# Need to close the inner LinearLayout
target_close = """            android:src="@android:drawable/ic_menu_send"
            app:tint="@android:color/white" />
    </LinearLayout>"""

replacement_close = """            android:src="@android:drawable/ic_menu_send"
            app:tint="@android:color/white" />
        </LinearLayout>
    </LinearLayout>"""

content = content.replace(target_close, replacement_close)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched bottom_sheet_comments.xml")
