import re

with open('app/src/main/res/layout/activity_manga_detail.xml', 'r') as f:
    content = f.read()

new_layout = """<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="?android:attr/colorBackground"
    android:layoutDirection="rtl">

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appBarLayout"
        android:layout_width="match_parent"
        android:layout_height="400dp"
        android:background="@android:color/transparent"
        app:elevation="0dp">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:contentScrim="?attr/colorSurface"
            app:layout_scrollFlags="scroll|exitUntilCollapsed|snap">

            <!-- Background Blurred Image -->
            <ImageView
                android:id="@+id/mangaCoverBlur"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                app:layout_collapseMode="parallax"
                app:layout_collapseParallaxMultiplier="0.7" />
                
            <View
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="#66000000"
                app:layout_collapseMode="parallax" />

            <View
                android:layout_width="match_parent"
                android:layout_height="150dp"
                android:layout_gravity="bottom"
                android:background="@drawable/gradient_bottom"
                app:layout_collapseMode="pin" />

            <!-- Foreground Cover Image (Floating) -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="140dp"
                android:layout_height="200dp"
                android:layout_gravity="center"
                app:cardCornerRadius="16dp"
                app:cardElevation="12dp"
                app:layout_collapseMode="parallax"
                app:layout_collapseParallaxMultiplier="0.3">
                <ImageView
                    android:id="@+id/mangaCover"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:scaleType="centerCrop" />
            </com.google.android.material.card.MaterialCardView>

            <!-- Custom Toolbar -->
            <androidx.appcompat.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:contentInsetStartWithNavigation="0dp"
                app:layout_collapseMode="pin">

                <RelativeLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent">

                    <ImageView
                        android:id="@+id/btnBack"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:padding="12dp"
                        android:src="@drawable/ic_back_arrow"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        android:layout_centerVertical="true"
                        app:tint="#FFFFFF" />

                    <TextView
                        android:id="@+id/toolbarTitle"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_toEndOf="@id/btnBack"
                        android:layout_toStartOf="@id/btnDownloadMultiple"
                        android:layout_centerVertical="true"
                        android:ellipsize="end"
                        android:maxLines="1"
                        android:text="Manga Title"
                        android:textColor="#FFFFFF"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:layout_marginStart="8dp"
                        android:alpha="0" />

                    <ImageView
                        android:id="@+id/btnDownloadMultiple"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:padding="12dp"
                        android:layout_alignParentEnd="true"
                        android:layout_centerVertical="true"
                        android:src="@drawable/ic_drawer_download"
                        android:background="?attr/selectableItemBackgroundBorderless"
                        app:tint="#FFFFFF" />
                </RelativeLayout>
            </androidx.appcompat.widget.Toolbar>
        </com.google.android.material.appbar.CollapsingToolbarLayout>
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.core.widget.NestedScrollView
        android:id="@+id/nestedScrollView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:fillViewport="true"
        android:background="?attr/colorSurface"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:paddingBottom="100dp">

            <!-- Content Card -->
            <LinearLayout
                android:id="@+id/detailsContainer"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="vertical"
                android:paddingTop="24dp"
                android:background="@drawable/bg_bottom_sheet_rounded">

                <TextView
                    android:id="@+id/mangaTitleDetail"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:paddingHorizontal="24dp"
                    android:text="Manga Title"
                    android:textColor="?attr/colorOnSurface"
                    android:textSize="24sp"
                    android:textStyle="bold"
                    android:gravity="center_horizontal" />

                <TextView
                    android:id="@+id/mangaAuthor"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="4dp"
                    android:paddingHorizontal="24dp"
                    android:text="Author Name"
                    android:textColor="?attr/colorOnSurfaceVariant"
                    android:textSize="14sp"
                    android:gravity="center_horizontal" />

                <!-- Main Actions (Modern Pills) -->
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:gravity="center"
                    android:paddingHorizontal="16dp"
                    android:layout_marginTop="24dp">

                    <LinearLayout
                        android:id="@+id/btnFavoriteContainer"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:orientation="vertical"
                        android:gravity="center"
                        android:layout_marginHorizontal="16dp"
                        android:clickable="true"
                        android:focusable="true">
                        <com.google.android.material.card.MaterialCardView
                            android:layout_width="48dp"
                            android:layout_height="48dp"
                            app:cardCornerRadius="24dp"
                            app:cardElevation="0dp"
                            app:cardBackgroundColor="?attr/colorSurfaceVariant">
                            <ImageView
                                android:id="@+id/btnFavorite"
                                android:layout_width="24dp"
                                android:layout_height="24dp"
                                android:layout_gravity="center"
                                android:src="@drawable/ic_favorite_border"
                                app:tint="?attr/colorOnSurface" />
                        </com.google.android.material.card.MaterialCardView>
                        <TextView
                            android:id="@+id/tvFavoriteText"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="8dp"
                            android:text="مفضلة"
                            android:textColor="?attr/colorOnSurface"
                            android:textSize="12sp"
                            android:textStyle="bold" />
                    </LinearLayout>

                    <LinearLayout
                        android:id="@+id/btnCommentsContainer"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:orientation="vertical"
                        android:gravity="center"
                        android:layout_marginHorizontal="16dp"
                        android:clickable="true"
                        android:focusable="true">
                        <com.google.android.material.card.MaterialCardView
                            android:layout_width="48dp"
                            android:layout_height="48dp"
                            app:cardCornerRadius="24dp"
                            app:cardElevation="0dp"
                            app:cardBackgroundColor="?attr/colorPrimary">
                            <ImageView
                                android:id="@+id/btnComments"
                                android:layout_width="24dp"
                                android:layout_height="24dp"
                                android:layout_gravity="center"
                                android:src="@drawable/ic_comments"
                                app:tint="?attr/colorOnPrimary" />
                        </com.google.android.material.card.MaterialCardView>
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="8dp"
                            android:text="تعليقات"
                            android:textColor="?attr/colorOnSurface"
                            android:textSize="12sp"
                            android:textStyle="bold" />
                    </LinearLayout>

                    <LinearLayout
                        android:id="@+id/btnMyList"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:orientation="vertical"
                        android:gravity="center"
                        android:layout_marginHorizontal="16dp"
                        android:clickable="true"
                        android:focusable="true">
                        <com.google.android.material.card.MaterialCardView
                            android:layout_width="48dp"
                            android:layout_height="48dp"
                            app:cardCornerRadius="24dp"
                            app:cardElevation="0dp"
                            app:cardBackgroundColor="?attr/colorSurfaceVariant">
                            <ImageView
                                android:layout_width="24dp"
                                android:layout_height="24dp"
                                android:layout_gravity="center"
                                android:src="@drawable/ic_list"
                                app:tint="?attr/colorOnSurface" />
                        </com.google.android.material.card.MaterialCardView>
                        <TextView
                            android:id="@+id/tvMyListStatus"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="8dp"
                            android:text="قائمتي"
                            android:textColor="?attr/colorOnSurface"
                            android:textSize="12sp"
                            android:textStyle="bold" />
                    </LinearLayout>
                </LinearLayout>

                <!-- Status & Rating Grid -->
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:paddingHorizontal="24dp"
                    android:layout_marginTop="24dp"
                    android:weightSum="3">
                    
                    <LinearLayout
                        android:layout_width="0dp"
                        android:layout_weight="1"
                        android:layout_height="wrap_content"
                        android:orientation="vertical"
                        android:gravity="center">
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="التقييم العام"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                        <LinearLayout
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:orientation="horizontal"
                            android:gravity="center_vertical">
                            <TextView
                                android:id="@+id/tvGlobalRating"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="--"
                                android:textColor="#FBC02D"
                                android:textSize="16sp"
                                android:textStyle="bold" />
                            <ImageView
                                android:layout_width="14dp"
                                android:layout_height="14dp"
                                android:layout_marginStart="4dp"
                                android:src="@android:drawable/star_on"
                                app:tint="#FBC02D" />
                        </LinearLayout>
                        <TextView
                            android:id="@+id/tvGlobalRatingCount"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="0 صوت"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="10sp" />
                    </LinearLayout>

                    <LinearLayout
                        android:layout_width="0dp"
                        android:layout_weight="1"
                        android:layout_height="wrap_content"
                        android:orientation="vertical"
                        android:gravity="center">
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="AniList"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                        <LinearLayout
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:orientation="horizontal"
                            android:gravity="center_vertical">
                            <TextView
                                android:id="@+id/tvALRating"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="--"
                                android:textColor="#4CAF50"
                                android:textSize="16sp"
                                android:textStyle="bold" />
                            <ImageView
                                android:layout_width="14dp"
                                android:layout_height="14dp"
                                android:layout_marginStart="4dp"
                                android:src="@android:drawable/star_on"
                                app:tint="#4CAF50" />
                        </LinearLayout>
                        <TextView
                            android:id="@+id/tvALRatingCount"
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="0 صوت"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="10sp" />
                    </LinearLayout>

                    <LinearLayout
                        android:id="@+id/btnUserRating"
                        android:layout_width="0dp"
                        android:layout_weight="1"
                        android:layout_height="wrap_content"
                        android:orientation="vertical"
                        android:gravity="center"
                        android:clickable="true"
                        android:focusable="true"
                        android:background="?attr/selectableItemBackgroundBorderless">
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="تقييمك"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                        <LinearLayout
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:orientation="horizontal"
                            android:gravity="center_vertical">
                            <TextView
                                android:id="@+id/tvUserRating"
                                android:layout_width="wrap_content"
                                android:layout_height="wrap_content"
                                android:text="--"
                                android:textColor="#FF9800"
                                android:textSize="16sp"
                                android:textStyle="bold" />
                            <ImageView
                                android:id="@+id/ivUserRatingStar"
                                android:layout_width="14dp"
                                android:layout_height="14dp"
                                android:layout_marginStart="4dp"
                                android:src="@android:drawable/star_on"
                                app:tint="#FF9800" />
                        </LinearLayout>
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:text="اضغط للتقييم"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="10sp" />
                    </LinearLayout>
                </LinearLayout>
                
                <View
                    android:layout_width="match_parent"
                    android:layout_height="1dp"
                    android:layout_marginHorizontal="24dp"
                    android:layout_marginTop="20dp"
                    android:background="?attr/colorSurfaceVariant"
                    android:alpha="0.3" />

                <!-- AniList Extra Info -->
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:paddingHorizontal="24dp"
                    android:paddingVertical="16dp"
                    android:orientation="vertical">
                    
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal">
                        <TextView
                            android:id="@+id/tvAniListAuthor"
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="المؤلف: --"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                        <TextView
                            android:id="@+id/tvAniListArtist"
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="الرسام: --"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                    </LinearLayout>
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:layout_marginTop="4dp">
                        <TextView
                            android:id="@+id/tvAniListCountry"
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="المنشأ: --"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                        <TextView
                            android:id="@+id/tvAniListDates"
                            android:layout_width="0dp"
                            android:layout_height="wrap_content"
                            android:layout_weight="1"
                            android:text="الحالة: --"
                            android:textColor="?attr/colorOnSurfaceVariant"
                            android:textSize="12sp" />
                    </LinearLayout>
                </LinearLayout>

                <TextView
                    android:id="@+id/mangaDescription"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:paddingHorizontal="24dp"
                    android:lineSpacingExtra="6dp"
                    android:textColor="?attr/colorOnSurface"
                    android:textSize="15sp" />

                <!-- Tabs & Chapters -->
                <com.google.android.material.tabs.TabLayout
                    android:id="@+id/tabLayout"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="24dp"
                    android:background="@android:color/transparent"
                    app:tabIndicatorColor="?attr/colorPrimary"
                    app:tabSelectedTextColor="?attr/colorPrimary"
                    app:tabTextColor="?attr/colorOnSurfaceVariant"
                    app:tabMode="fixed"
                    app:tabGravity="fill" />
                
                <FrameLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:minHeight="300dp">
                    
                    <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
                        android:id="@+id/swipeRefreshLayout"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent">
                        
                        <androidx.recyclerview.widget.RecyclerView
                            android:id="@+id/chaptersRecyclerView"
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"
                            android:clipToPadding="false"
                            android:paddingTop="12dp" />
                    </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>

                    <ProgressBar
                        android:id="@+id/progressBar"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:layout_gravity="center"
                        android:indeterminateTint="?attr/colorPrimary" />
                </FrameLayout>
            </LinearLayout>
        </LinearLayout>
    </androidx.core.widget.NestedScrollView>
    
    <!-- We need these invisible so old code doesn't crash if they try to find them directly without checking -->
    <TextView android:id="@+id/mangaStatus" android:layout_width="0dp" android:layout_height="0dp" android:visibility="gone"/>
    <TextView android:id="@+id/tvAniListFormat" android:layout_width="0dp" android:layout_height="0dp" android:visibility="gone"/>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
"""

with open('app/src/main/res/layout/activity_manga_detail.xml', 'w') as f:
    f.write(new_layout)
