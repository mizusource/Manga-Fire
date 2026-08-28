import re

with open('app/src/main/res/layout/activity_main.xml', 'r') as f:
    content = f.read()

# Add announcement layout below AppBarLayout
announcement = '''
        </com.google.android.material.appbar.AppBarLayout>

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

        <androidx.swiperefreshlayout.widget.SwipeRefreshLayout'''
content = re.sub(r'</com\.google\.android\.material\.appbar\.AppBarLayout>.*?<androidx\.swiperefreshlayout\.widget\.SwipeRefreshLayout', announcement, content, flags=re.DOTALL)

# Add maintenance layout at the end of DrawerLayout
maintenance = '''
    </com.google.android.material.navigation.NavigationView>

    <LinearLayout
        android:id="@+id/maintenanceLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="?attr/colorSurface"
        android:orientation="vertical"
        android:gravity="center"
        android:padding="32dp"
        android:visibility="gone"
        android:elevation="100dp"
        android:clickable="true"
        android:focusable="true">
        <ImageView
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:src="@android:drawable/ic_dialog_alert"
            app:tint="?attr/colorError" />
        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="وضع الصيانة"
            android:textSize="24sp"
            android:textStyle="bold"
            android:textColor="?attr/colorOnSurface"
            android:layout_marginTop="16dp" />
        <TextView
            android:id="@+id/tvMaintenanceMessage"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="التطبيق قيد الصيانة حالياً. يرجى العودة لاحقاً."
            android:textSize="16sp"
            android:textColor="?attr/colorOnSurfaceVariant"
            android:textAlignment="center"
            android:layout_marginTop="8dp" />
    </LinearLayout>

</androidx.drawerlayout.widget.DrawerLayout>'''
content = re.sub(r'</com\.google\.android\.material\.navigation\.NavigationView>.*?</androidx\.drawerlayout\.widget\.DrawerLayout>', maintenance, content, flags=re.DOTALL)

with open('app/src/main/res/layout/activity_main.xml', 'w') as f:
    f.write(content)
