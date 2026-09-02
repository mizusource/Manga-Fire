import re

filepath = 'app/src/main/res/layout/item_notification.xml'
with open(filepath, 'r') as f:
    content = f.read()

# Add tvDate next to tvSender using a RelativeLayout
new_layout = """    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <TextView
            android:id="@+id/tvSender"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentStart="true"
            android:textColor="#FFFFFF"
            android:textStyle="bold"
            android:textSize="16sp" />
        <TextView
            android:id="@+id/tvDate"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_alignParentEnd="true"
            android:textColor="#80FFFFFF"
            android:textSize="12sp" />
    </RelativeLayout>
    <TextView
        android:id="@+id/tvMessage"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textColor="#B3FFFFFF"
        android:layout_marginTop="4dp"
        android:textSize="14sp" />"""

content = re.sub(r'<TextView\s+android:id="@+id/tvSender".*?<TextView\s+android:id="@+id/tvMessage"[^>]*?/>', new_layout, content, flags=re.DOTALL)

with open(filepath, 'w') as f:
    f.write(content)
print("Updated item_notification.xml")
