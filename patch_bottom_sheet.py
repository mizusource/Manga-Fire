import re

with open("app/src/main/res/layout/bottom_sheet_comments.xml", "r") as f:
    text = f.read()

# I will replace everything after </RelativeLayout> with our include and the login prompt.
pattern = r'</RelativeLayout>.*'

replacement = """</RelativeLayout>
    
    <FrameLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        
        <!-- Input Area (Composer) -->
        <include 
            android:id="@+id/widgetComposer"
            layout="@layout/layout_widget_composer" />

        <!-- Login Prompt Overlay -->
        <LinearLayout
            android:id="@+id/layoutLoginPrompt"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="?attr/colorSurfaceVariant"
            android:elevation="14dp"
            android:orientation="horizontal"
            android:padding="16dp"
            android:visibility="gone"
            android:gravity="center_vertical">
            <TextView
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="سجل دخولك لتتمكن من التفاعل"
                android:textSize="14sp"
                android:textStyle="bold"
                android:textColor="?attr/colorOnSurfaceVariant"/>
            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnLoginPrompt"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="تسجيل الدخول"
                app:cornerRadius="20dp" />
        </LinearLayout>
        
    </FrameLayout>
</LinearLayout>
"""

new_text = re.sub(pattern, replacement, text, flags=re.DOTALL)

with open("app/src/main/res/layout/bottom_sheet_comments.xml", "w") as f:
    f.write(new_text)
