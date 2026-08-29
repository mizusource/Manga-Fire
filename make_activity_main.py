xml_content = """<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/main_coordinator"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layoutDirection="rtl"
    android:background="?android:attr/colorBackground">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@android:color/transparent"
        app:elevation="0dp">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            app:layout_scrollFlags="scroll|enterAlways"
            app:contentScrim="?attr/colorSurface">

            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:paddingTop="50dp"
                android:paddingBottom="10dp"
                android:paddingHorizontal="24dp">
                
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="مرحباً بعودتك،"
                    android:textSize="14sp"
                    android:textColor="?attr/colorOnSurfaceVariant" />
                    
                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="استكشف المانجا"
                    android:textSize="28sp"
                    android:textStyle="bold"
                    android:textColor="?attr/colorOnSurface"
                    android:layout_marginTop="4dp" />
                    
            </LinearLayout>

            <androidx.appcompat.widget.Toolbar
                android:id="@+id/mainToolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_collapseMode="pin">
                <RelativeLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">
                    
                    <com.google.android.material.imageview.ShapeableImageView
                        android:id="@+id/btnProfileAvatar"
                        android:layout_width="36dp"
                        android:layout_height="36dp"
                        android:layout_alignParentStart="true"
                        android:layout_centerVertical="true"
                        android:layout_marginStart="16dp"
                        android:src="@drawable/ic_person"
                        android:scaleType="centerCrop"
                        app:shapeAppearanceOverlay="@style/CircleImage"
                        android:background="?attr/colorSurfaceVariant"
                        app:strokeWidth="1dp"
                        app:strokeColor="?attr/colorOutline" />
                        
                    <ImageView
                        android:id="@+id/btnToggleView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_toStartOf="@id/btnSearch"
                        android:layout_centerVertical="true"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        android:padding="12dp"
                        android:src="@android:drawable/ic_menu_sort_by_size"
                        app:tint="?attr/colorOnSurface" />
                        
                    <ImageView
                        android:id="@+id/btnSearch"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_alignParentEnd="true"
                        android:layout_centerVertical="true"
                        android:layout_marginEnd="16dp"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        android:padding="12dp"
                        android:src="@drawable/ic_search"
                        app:tint="?attr/colorOnSurface" />
                </RelativeLayout>
            </androidx.appcompat.widget.Toolbar>
        </com.google.android.material.appbar.CollapsingToolbarLayout>
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
        android:id="@+id/swipeRefreshMain"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <FrameLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent">
            
            <androidx.recyclerview.widget.RecyclerView
                android:id="@+id/rvLatestUpdates"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:clipToPadding="false"
                android:paddingBottom="120dp"
                android:paddingTop="8dp" />
                
            <include layout="@layout/layout_shimmer_mangas" 
                android:id="@+id/mainShimmerView" 
                android:visibility="gone" />
        </FrameLayout>
    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>

    <include layout="@layout/layout_bottom_nav" />
</androidx.coordinatorlayout.widget.CoordinatorLayout>
"""

with open('app/src/main/res/layout/activity_main.xml', 'w') as f:
    f.write(xml_content)
