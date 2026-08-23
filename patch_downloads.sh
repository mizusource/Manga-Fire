sed -i '/<TextView/,$d' app/src/main/res/layout/activity_downloads.xml
cat << 'INNER' >> app/src/main/res/layout/activity_downloads.xml
        <!-- رسالة تظهر إذا كانت التنزيلات فارغة -->
        <LinearLayout
            android:id="@+id/emptyStateLayout"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical"
            android:gravity="center"
            android:padding="32dp"
            android:visibility="gone">

            <ImageView
                android:layout_width="100dp"
                android:layout_height="100dp"
                android:src="@drawable/ic_drawer_download"
                android:background="@drawable/circle_bg_purple"
                android:padding="24dp"
                app:tint="?attr/colorPrimary"
                android:layout_marginBottom="24dp" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="قائمة التحميل فارغة"
                android:textColor="?attr/colorOnSurface"
                android:textSize="20sp"
                android:textStyle="bold"
                android:layout_marginBottom="12dp" />

            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:text="لم تقم بتنزيل أي فصول بعد. يمكنك تنزيل الفصول لقراءتها لاحقاً بدون إنترنت."
                android:textColor="?attr/colorOnSurfaceVariant"
                android:textSize="14sp"
                android:textAlignment="center"
                android:lineSpacingExtra="4dp"
                android:layout_marginBottom="24dp" />

            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnExplore"
                android:layout_width="match_parent"
                android:layout_height="56dp"
                android:text="استكشاف المانجا"
                android:textColor="?attr/colorOnPrimary"
                android:textSize="16sp"
                app:cornerRadius="12dp"
                app:backgroundTint="?attr/colorPrimary" />
        </LinearLayout>
    </FrameLayout>
</LinearLayout>
INNER
