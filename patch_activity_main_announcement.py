import re

with open('app/src/main/res/layout/activity_main.xml', 'r') as f:
    content = f.read()

announcement = """
        <androidx.cardview.widget.CardView
            android:id="@+id/announcementBanner"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_margin="16dp"
            android:visibility="gone"
            app:cardBackgroundColor="#FFC107"
            app:cardCornerRadius="8dp"
            app:layout_behavior="@string/appbar_scrolling_view_behavior">
            <TextView
                android:id="@+id/tvAnnouncementText"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:padding="16dp"
                android:textColor="#000000"
                android:textSize="14sp"
                android:textStyle="bold"
                android:textAlignment="center"/>
        </androidx.cardview.widget.CardView>

"""

content = content.replace('<androidx.swiperefreshlayout.widget.SwipeRefreshLayout', announcement + '        <androidx.swiperefreshlayout.widget.SwipeRefreshLayout')

with open('app/src/main/res/layout/activity_main.xml', 'w') as f:
    f.write(content)

