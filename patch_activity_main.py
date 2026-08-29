import re

new_xml = """<?xml version="1.0" encoding="utf-8"?>
<androidx.drawerlayout.widget.DrawerLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/drawer_layout"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layoutDirection="rtl"
    android:background="#121212">

    <androidx.coordinatorlayout.widget.CoordinatorLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <com.google.android.material.appbar.AppBarLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@android:color/transparent"
            app:elevation="0dp">

            <com.google.android.material.appbar.CollapsingToolbarLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:layout_scrollFlags="scroll|enterAlways|enterAlwaysCollapsed"
                app:contentScrim="@android:color/transparent">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical">

                    <!-- Custom Toolbar / Header -->
                    <RelativeLayout
                        android:layout_width="match_parent"
                        android:layout_height="?attr/actionBarSize"
                        android:paddingHorizontal="8dp">

                        <ImageView
                            android:id="@+id/btnMenuToggle"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_alignParentStart="true"
                            android:layout_centerVertical="true"
                            android:background="?attr/selectableItemBackgroundBorderless"
                            android:padding="12dp"
                            android:src="@drawable/ic_menu"
                            app:tint="#FFFFFF" />

                        <ImageView
                            android:id="@+id/btnToggleView"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_alignParentEnd="true"
                            android:layout_centerVertical="true"
                            android:background="?attr/selectableItemBackgroundBorderless"
                            android:padding="12dp"
                            android:src="@android:drawable/ic_menu_sort_by_size"
                            app:tint="#FFFFFF" />

                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_centerInParent="true"
                            android:text="Manga Reader"
                            android:textColor="#FFFFFF"
                            android:textSize="20sp"
                            android:textStyle="bold" />

                    </RelativeLayout>

                    <!-- Hero Banner -->
                    <androidx.viewpager2.widget.ViewPager2
                        android:id="@+id/vpHeroBanner"
                        android:layout_width="match_parent"
                        android:layout_height="180dp"
                        android:layout_marginTop="8dp" />

                </LinearLayout>

            </com.google.android.material.appbar.CollapsingToolbarLayout>

            <!-- Search and Tabs Bar -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:paddingHorizontal="16dp"
                android:paddingVertical="8dp"
                android:gravity="center_vertical">

                <ImageView
                    android:id="@+id/btnSearch"
                    android:layout_width="40dp"
                    android:layout_height="40dp"
                    android:background="@drawable/bg_glass_icon"
                    android:padding="8dp"
                    android:src="@drawable/ic_search"
                    app:tint="#FFFFFF" />

                <Space
                    android:layout_width="0dp"
                    android:layout_height="wrap_content"
                    android:layout_weight="1" />

                <!-- Simple Tabs equivalent -->
                <LinearLayout
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:background="@drawable/bg_glassmorphism"
                    android:padding="4dp">

                    <TextView
                        android:id="@+id/tabLatest"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:paddingHorizontal="16dp"
                        android:paddingVertical="8dp"
                        android:text="آخر التحديثات"
                        android:textColor="#00E5FF"
                        android:textStyle="bold"
                        android:background="@drawable/bg_glassmorphism_active" />

                    <TextView
                        android:id="@+id/tabPopular"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:paddingHorizontal="16dp"
                        android:paddingVertical="8dp"
                        android:text="الأكثر شعبية"
                        android:textColor="#B3FFFFFF"
                        android:background="@android:color/transparent" />

                </LinearLayout>

                <ImageView
                    android:layout_width="40dp"
                    android:layout_height="40dp"
                    android:layout_marginStart="8dp"
                    android:background="@drawable/bg_glass_icon"
                    android:padding="8dp"
                    android:src="@drawable/ic_filter"
                    app:tint="#FFFFFF" />

            </LinearLayout>

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
                    android:padding="8dp"
                    android:layoutAnimation="@anim/layout_animation_fall_down" />

                <include layout="@layout/layout_shimmer_mangas" 
                    android:id="@+id/mainShimmerView" 
                    android:visibility="gone" />

            </FrameLayout>

        </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>

    </androidx.coordinatorlayout.widget.CoordinatorLayout>

    <com.google.android.material.navigation.NavigationView
        android:id="@+id/nav_view"
        android:layout_width="280dp"
        android:layout_height="match_parent"
        android:layout_gravity="start"
        android:background="@drawable/bg_drawer_glass"
        app:itemTextColor="#FFFFFF"
        app:itemIconTint="#00E5FF"
        app:menu="@menu/drawer_menu"
        app:headerLayout="@layout/nav_header"
        app:itemShapeAppearance="@style/ShapeAppearance.Material3.Corner.Medium"
        app:itemShapeFillColor="#1A00E5FF" />

</androidx.drawerlayout.widget.DrawerLayout>
"""

with open("app/src/main/res/layout/activity_main.xml", "w") as f:
    f.write(new_xml)
