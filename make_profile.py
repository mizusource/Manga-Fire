xml_content = """<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layoutDirection="rtl"
    android:background="?android:attr/colorBackground">

    <com.google.android.material.appbar.AppBarLayout
        android:layout_width="match_parent"
        android:layout_height="320dp"
        android:background="@android:color/transparent"
        app:elevation="0dp">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:contentScrim="?attr/colorSurface"
            app:layout_scrollFlags="scroll|exitUntilCollapsed">

            <!-- Deep Parallax Header -->
            <ImageView
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:src="@drawable/bg_appbar"
                android:scaleType="centerCrop"
                android:alpha="0.4"
                app:layout_collapseMode="parallax"
                app:layout_collapseParallaxMultiplier="0.5" />
                
            <View
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/bg_gradient_top_to_bottom"
                app:layout_collapseMode="parallax" />

            <!-- Profile Info centered -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center"
                android:orientation="vertical"
                android:gravity="center"
                android:paddingTop="30dp"
                app:layout_collapseMode="parallax">

                <RelativeLayout
                    android:layout_width="120dp"
                    android:layout_height="120dp">
                    
                    <com.google.android.material.imageview.ShapeableImageView
                        android:id="@+id/profileImage"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:src="@drawable/ic_person"
                        android:scaleType="centerCrop"
                        app:shapeAppearanceOverlay="@style/CircleImage"
                        android:background="?attr/colorSurfaceVariant"
                        app:strokeWidth="2dp"
                        app:strokeColor="?attr/colorPrimary" />

                    <ImageView
                        android:id="@+id/btnChangeBanner"
                        android:layout_width="32dp"
                        android:layout_height="32dp"
                        android:layout_alignParentBottom="true"
                        android:layout_alignParentEnd="true"
                        android:layout_marginEnd="4dp"
                        android:layout_marginBottom="4dp"
                        android:background="@drawable/circle_bg"
                        android:backgroundTint="?attr/colorPrimary"
                        android:padding="6dp"
                        android:src="@android:drawable/ic_menu_camera"
                        app:tint="@android:color/white" />
                </RelativeLayout>

                <TextView
                    android:id="@+id/tvUserName"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="16dp"
                    android:text="زائر"
                    android:textColor="#FFFFFF"
                    android:textSize="26sp"
                    android:textStyle="bold" />
                    
                <TextView
                    android:id="@+id/tvFullName"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp"
                    android:text="Guest Account"
                    android:textColor="#AAAAAA"
                    android:textSize="14sp" />
            </LinearLayout>

            <androidx.appcompat.widget.Toolbar
                android:id="@+id/profileToolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:layout_collapseMode="pin">
                <RelativeLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">
                    
                    <ImageView
                        android:id="@+id/btnSettings"
                        android:layout_width="48dp"
                        android:layout_height="48dp"
                        android:layout_alignParentStart="true"
                        android:layout_centerVertical="true"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        android:padding="12dp"
                        android:src="@drawable/ic_settings"
                        app:tint="#FFFFFF" />
                        
                    <!-- Hidden by default, shown if Admin -->
                    <ImageView
                        android:id="@+id/btnAdmin"
                        android:layout_width="48dp"
                        android:layout_height="48dp"
                        android:layout_toEndOf="@id/btnSettings"
                        android:layout_centerVertical="true"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        android:padding="12dp"
                        android:src="@android:drawable/ic_menu_manage"
                        app:tint="#FFC107"
                        android:visibility="gone" />

                    <ImageView
                        android:id="@+id/btnLogout"
                        android:layout_width="48dp"
                        android:layout_height="48dp"
                        android:layout_alignParentEnd="true"
                        android:layout_centerVertical="true"
                        android:layout_marginEnd="8dp"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        android:padding="12dp"
                        android:src="@drawable/ic_logout"
                        app:tint="#F44336" />
                </RelativeLayout>
            </androidx.appcompat.widget.Toolbar>
        </com.google.android.material.appbar.CollapsingToolbarLayout>
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.core.widget.NestedScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:clipToPadding="false"
        android:paddingBottom="100dp"
        android:background="?android:attr/colorBackground"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <!-- Edit Profile Button -->
            <com.google.android.material.button.MaterialButton
                android:id="@+id/btnEditProfile"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_gravity="center_horizontal"
                android:layout_marginTop="-24dp"
                android:text="تعديل الحساب"
                android:textColor="?attr/colorOnPrimary"
                app:backgroundTint="?attr/colorPrimary"
                app:cornerRadius="20dp"
                android:elevation="8dp" />

            <!-- Stats Card Glassmorphism Style -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginHorizontal="24dp"
                android:layout_marginTop="24dp"
                android:layout_marginBottom="24dp"
                app:cardBackgroundColor="?attr/colorSurface"
                app:cardCornerRadius="24dp"
                app:cardElevation="0dp"
                app:strokeWidth="1dp"
                app:strokeColor="?attr/colorOutline">
                
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="24dp">
                    
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="إحصائيات المكتبة"
                        android:textColor="?attr/colorOnSurface"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:layout_marginBottom="24dp" />

                    <RelativeLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content">
                        
                        <com.fire.mangareader.utils.DonutChartView
                            android:id="@+id/donutChart"
                            android:layout_width="120dp"
                            android:layout_height="120dp"
                            android:layout_alignParentStart="true"
                            android:layout_centerVertical="true" />
                            
                        <LinearLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:layout_toEndOf="@id/donutChart"
                            android:layout_centerVertical="true"
                            android:layout_marginStart="24dp"
                            android:orientation="vertical">
                            
                            <TextView android:id="@+id/legendFav" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="■ المفضلة" android:textColor="#5A9CC4" android:textSize="14sp" android:textStyle="bold" />
                            <TextView android:id="@+id/legendRead" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="■ اشاهدها حاليا" android:textColor="#44A85F" android:textSize="14sp" android:textStyle="bold" android:layout_marginTop="12dp" />
                            <TextView android:id="@+id/legendPlan" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="■ ارغب بمشاهدتها" android:textColor="#C33B32" android:textSize="14sp" android:textStyle="bold" android:layout_marginTop="12dp" />
                            <TextView android:id="@+id/legendComp" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="■ تم مشاهدتها" android:textColor="#6A3CC4" android:textSize="14sp" android:textStyle="bold" android:layout_marginTop="12dp" />
                            <TextView android:id="@+id/legendDrop" android:layout_width="wrap_content" android:layout_height="wrap_content" android:text="■ لا ارغب بمشاهدتها" android:textColor="#8E24AA" android:textSize="14sp" android:textStyle="bold" android:layout_marginTop="12dp" />
                        </LinearLayout>
                    </RelativeLayout>
                    
                    <View 
                        android:layout_width="match_parent"
                        android:layout_height="1dp"
                        android:background="?attr/colorOutline"
                        android:layout_marginVertical="20dp"
                        android:alpha="0.2" />
                        
                    <TextView
                        android:id="@+id/tvTotalChapters"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="الفصول المقروءة: 0"
                        android:textColor="?attr/colorPrimary"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:gravity="center" />
                        
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
        </LinearLayout>
    </androidx.core.widget.NestedScrollView>

    <include layout="@layout/layout_bottom_nav" />

</androidx.coordinatorlayout.widget.CoordinatorLayout>
"""

with open('app/src/main/res/layout/fragment_profile.xml', 'w') as f:
    f.write(xml_content)
