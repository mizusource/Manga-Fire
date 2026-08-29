xml_content = """<?xml version="1.0" encoding="utf-8"?>
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
        android:layout_height="500dp"
        android:background="@android:color/transparent"
        app:elevation="0dp">

        <com.google.android.material.appbar.CollapsingToolbarLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:contentScrim="?android:attr/colorBackground"
            app:layout_scrollFlags="scroll|exitUntilCollapsed|snap">

            <!-- Deep Parallax Blur -->
            <ImageView
                android:id="@+id/mangaCoverBlur"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop"
                android:alpha="0.6"
                app:layout_collapseMode="parallax"
                app:layout_collapseParallaxMultiplier="0.5" />
                
            <!-- Fade to background color -->
            <View
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:background="@drawable/bg_gradient_top_to_bottom"
                app:layout_collapseMode="parallax" />

            <View
                android:layout_width="match_parent"
                android:layout_height="250dp"
                android:layout_gravity="bottom"
                android:background="@drawable/gradient_bottom"
                app:layout_collapseMode="pin" />

            <!-- Stunning Header Content -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="bottom"
                android:orientation="vertical"
                android:gravity="center"
                android:paddingBottom="24dp"
                app:layout_collapseMode="parallax"
                app:layout_collapseParallaxMultiplier="0.2">

                <com.google.android.material.card.MaterialCardView
                    android:layout_width="160dp"
                    android:layout_height="230dp"
                    app:cardCornerRadius="24dp"
                    app:cardElevation="24dp"
                    app:strokeWidth="2dp"
                    app:strokeColor="#33FFFFFF">
                    <ImageView
                        android:id="@+id/mangaCover"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:scaleType="centerCrop" />
                </com.google.android.material.card.MaterialCardView>

                <TextView
                    android:id="@+id/mangaTitle"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="20dp"
                    android:layout_marginHorizontal="24dp"
                    android:gravity="center"
                    android:ellipsize="end"
                    android:maxLines="2"
                    android:text="Manga Title"
                    android:textColor="#FFFFFF"
                    android:textSize="26sp"
                    android:textStyle="bold" />

                <TextView
                    android:id="@+id/mangaAuthor"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_marginTop="8dp"
                    android:text="المؤلف"
                    android:textColor="#BBBBBB"
                    android:textSize="14sp" />
                    
            </LinearLayout>

            <!-- Sticky Toolbar -->
            <androidx.appcompat.widget.Toolbar
                android:id="@+id/toolbar"
                android:layout_width="match_parent"
                android:layout_height="?attr/actionBarSize"
                app:contentInsetStartWithNavigation="0dp"
                app:layout_collapseMode="pin">
                
                <RelativeLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:paddingHorizontal="8dp">

                    <com.google.android.material.card.MaterialCardView
                        android:id="@+id/btnBackContainer"
                        android:layout_width="48dp"
                        android:layout_height="48dp"
                        android:layout_centerVertical="true"
                        android:layout_alignParentStart="true"
                        app:cardBackgroundColor="#33000000"
                        app:cardCornerRadius="24dp"
                        app:cardElevation="0dp"
                        app:strokeWidth="1dp"
                        app:strokeColor="#33FFFFFF">
                        
                        <ImageView
                            android:id="@+id/btnBack"
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"
                            android:padding="12dp"
                            android:src="@drawable/ic_back_arrow"
                            android:background="?attr/selectableItemBackgroundBorderless"
                            app:tint="#FFFFFF" />
                    </com.google.android.material.card.MaterialCardView>

                    <TextView
                        android:id="@+id/toolbarTitle"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_toEndOf="@id/btnBackContainer"
                        android:layout_toStartOf="@id/btnDownloadMultipleContainer"
                        android:layout_centerVertical="true"
                        android:ellipsize="end"
                        android:maxLines="1"
                        android:text="Title"
                        android:textColor="#FFFFFF"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        android:layout_marginStart="12dp"
                        android:layout_marginEnd="12dp"
                        android:visibility="gone" />

                    <com.google.android.material.card.MaterialCardView
                        android:id="@+id/btnDownloadMultipleContainer"
                        android:layout_width="48dp"
                        android:layout_height="48dp"
                        android:layout_centerVertical="true"
                        android:layout_alignParentEnd="true"
                        app:cardBackgroundColor="#33000000"
                        app:cardCornerRadius="24dp"
                        app:cardElevation="0dp"
                        app:strokeWidth="1dp"
                        app:strokeColor="#33FFFFFF">
                        
                        <ImageView
                            android:id="@+id/btnDownloadMultiple"
                            android:layout_width="match_parent"
                            android:layout_height="match_parent"
                            android:padding="12dp"
                            android:src="@drawable/ic_drawer_download"
                            android:background="?attr/selectableItemBackgroundBorderless"
                            app:tint="#FFFFFF" />
                    </com.google.android.material.card.MaterialCardView>
                </RelativeLayout>
            </androidx.appcompat.widget.Toolbar>
        </com.google.android.material.appbar.CollapsingToolbarLayout>
    </com.google.android.material.appbar.AppBarLayout>

    <androidx.core.widget.NestedScrollView
        android:id="@+id/nestedScrollView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:fillViewport="true"
        android:background="?android:attr/colorBackground"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical">

            <!-- Innovative Action Bar (Bento Style) -->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:paddingHorizontal="24dp"
                android:paddingVertical="16dp"
                android:weightSum="3"
                android:baselineAligned="false">
                
                <com.google.android.material.card.MaterialCardView
                    android:id="@+id/btnChangeStatus"
                    android:layout_width="0dp"
                    android:layout_height="70dp"
                    android:layout_weight="1"
                    android:layout_marginEnd="8dp"
                    app:cardBackgroundColor="?attr/colorPrimaryContainer"
                    app:cardCornerRadius="20dp"
                    app:cardElevation="0dp">
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:gravity="center"
                        android:orientation="vertical"
                        android:background="?attr/selectableItemBackground">
                        <ImageView
                            android:layout_width="24dp"
                            android:layout_height="24dp"
                            android:src="@android:drawable/ic_menu_add"
                            app:tint="?attr/colorPrimary" />
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:text="محفوظ"
                            android:textColor="?attr/colorPrimary"
                            android:textSize="12sp"
                            android:textStyle="bold"/>
                    </LinearLayout>
                </com.google.android.material.card.MaterialCardView>
                
                <com.google.android.material.card.MaterialCardView
                    android:id="@+id/btnComments"
                    android:layout_width="0dp"
                    android:layout_height="70dp"
                    android:layout_weight="1"
                    android:layout_marginHorizontal="8dp"
                    app:cardBackgroundColor="?attr/colorSecondaryContainer"
                    app:cardCornerRadius="20dp"
                    app:cardElevation="0dp">
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:gravity="center"
                        android:orientation="vertical"
                        android:background="?attr/selectableItemBackground">
                        <ImageView
                            android:layout_width="24dp"
                            android:layout_height="24dp"
                            android:src="@drawable/ic_comments"
                            app:tint="?attr/colorSecondary" />
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:text="النقاشات"
                            android:textColor="?attr/colorSecondary"
                            android:textSize="12sp"
                            android:textStyle="bold"/>
                    </LinearLayout>
                </com.google.android.material.card.MaterialCardView>
                
                <com.google.android.material.card.MaterialCardView
                    android:id="@+id/btnResumeReading"
                    android:layout_width="0dp"
                    android:layout_height="70dp"
                    android:layout_weight="1"
                    android:layout_marginStart="8dp"
                    app:cardBackgroundColor="?attr/colorPrimary"
                    app:cardCornerRadius="20dp"
                    app:cardElevation="4dp">
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:gravity="center"
                        android:orientation="vertical"
                        android:background="?attr/selectableItemBackground">
                        <ImageView
                            android:layout_width="24dp"
                            android:layout_height="24dp"
                            android:src="@android:drawable/ic_media_play"
                            app:tint="?attr/colorOnPrimary" />
                        <TextView
                            android:layout_width="wrap_content"
                            android:layout_height="wrap_content"
                            android:layout_marginTop="4dp"
                            android:text="اقرأ الآن"
                            android:textColor="?attr/colorOnPrimary"
                            android:textSize="12sp"
                            android:textStyle="bold"/>
                    </LinearLayout>
                </com.google.android.material.card.MaterialCardView>
            </LinearLayout>

            <!-- Tabs -->
            <com.google.android.material.tabs.TabLayout
                android:id="@+id/tabLayout"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@android:color/transparent"
                app:tabIndicatorColor="?attr/colorPrimary"
                app:tabIndicatorHeight="3dp"
                app:tabSelectedTextColor="?attr/colorPrimary"
                app:tabTextColor="?attr/colorOnSurfaceVariant"
                app:tabMode="fixed"
                app:tabGravity="fill"
                app:tabIndicatorAnimationMode="elastic">
                <com.google.android.material.tabs.TabItem
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="نظرة عامة" />
                <com.google.android.material.tabs.TabItem
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:text="الفصول" />
            </com.google.android.material.tabs.TabLayout>
            
            <View
                android:layout_width="match_parent"
                android:layout_height="1dp"
                android:background="?attr/colorOutline"
                android:alpha="0.1" />

            <FrameLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:minHeight="400dp"
                android:paddingTop="16dp">
                
                <!-- Details Container -->
                <LinearLayout
                    android:id="@+id/detailsContainer"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:visibility="visible">
                    
                    <!-- Beautiful Bento Grid for Stats -->
                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:orientation="horizontal"
                        android:paddingHorizontal="24dp"
                        android:baselineAligned="false">
                        
                        <!-- AniList Score -->
                        <com.google.android.material.card.MaterialCardView
                            android:layout_width="0dp"
                            android:layout_height="80dp"
                            android:layout_weight="1"
                            android:layout_marginEnd="8dp"
                            app:cardBackgroundColor="?attr/colorSurface"
                            app:cardCornerRadius="16dp"
                            app:strokeWidth="1dp"
                            app:strokeColor="?attr/colorOutline">
                            <LinearLayout
                                android:layout_width="match_parent"
                                android:layout_height="match_parent"
                                android:gravity="center"
                                android:orientation="vertical">
                                <TextView
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="AniList"
                                    android:textColor="?attr/colorOnSurfaceVariant"
                                    android:textSize="12sp" />
                                <TextView
                                    android:id="@+id/tvAniListScore"
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="--"
                                    android:textColor="#4CAF50"
                                    android:textSize="18sp"
                                    android:textStyle="bold"
                                    android:layout_marginTop="4dp"/>
                            </LinearLayout>
                        </com.google.android.material.card.MaterialCardView>

                        <!-- Global Score -->
                        <com.google.android.material.card.MaterialCardView
                            android:layout_width="0dp"
                            android:layout_height="80dp"
                            android:layout_weight="1"
                            android:layout_marginHorizontal="4dp"
                            app:cardBackgroundColor="?attr/colorSurface"
                            app:cardCornerRadius="16dp"
                            app:strokeWidth="1dp"
                            app:strokeColor="?attr/colorOutline">
                            <LinearLayout
                                android:layout_width="match_parent"
                                android:layout_height="match_parent"
                                android:gravity="center"
                                android:orientation="vertical">
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
                                    android:gravity="center_vertical"
                                    android:layout_marginTop="4dp">
                                    <TextView
                                        android:id="@+id/tvGlobalRating"
                                        android:layout_width="wrap_content"
                                        android:layout_height="wrap_content"
                                        android:text="--"
                                        android:textColor="#FFC107"
                                        android:textSize="18sp"
                                        android:textStyle="bold" />
                                    <ImageView
                                        android:layout_width="14dp"
                                        android:layout_height="14dp"
                                        android:layout_marginStart="4dp"
                                        android:src="@android:drawable/star_on"
                                        app:tint="#FFC107" />
                                </LinearLayout>
                            </LinearLayout>
                        </com.google.android.material.card.MaterialCardView>

                        <!-- User Rate -->
                        <com.google.android.material.card.MaterialCardView
                            android:id="@+id/btnRate"
                            android:layout_width="0dp"
                            android:layout_height="80dp"
                            android:layout_weight="1"
                            android:layout_marginStart="8dp"
                            app:cardBackgroundColor="?attr/colorSurfaceVariant"
                            app:cardCornerRadius="16dp"
                            app:strokeWidth="0dp">
                            <LinearLayout
                                android:layout_width="match_parent"
                                android:layout_height="match_parent"
                                android:gravity="center"
                                android:orientation="vertical"
                                android:background="?attr/selectableItemBackground">
                                <TextView
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="تقييمك"
                                    android:textColor="?attr/colorOnSurfaceVariant"
                                    android:textSize="12sp" />
                                <TextView
                                    android:id="@+id/tvUserRating"
                                    android:layout_width="wrap_content"
                                    android:layout_height="wrap_content"
                                    android:text="قيم الآن"
                                    android:textColor="?attr/colorPrimary"
                                    android:textSize="14sp"
                                    android:textStyle="bold"
                                    android:layout_marginTop="4dp"/>
                            </LinearLayout>
                        </com.google.android.material.card.MaterialCardView>
                    </LinearLayout>
                    
                    <com.google.android.material.card.MaterialCardView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginHorizontal="24dp"
                        android:layout_marginTop="16dp"
                        app:cardBackgroundColor="?attr/colorSurface"
                        app:cardCornerRadius="16dp"
                        app:strokeWidth="1dp"
                        app:strokeColor="?attr/colorOutline">
                        <LinearLayout
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:orientation="vertical"
                            android:padding="16dp">
                            <TextView
                                android:id="@+id/tvAniListAuthor"
                                android:layout_width="match_parent"
                                android:layout_height="wrap_content"
                                android:text="المؤلف: --"
                                android:textColor="?attr/colorOnSurface"
                                android:textSize="13sp"
                                android:layout_marginBottom="4dp"/>
                            <TextView
                                android:id="@+id/tvAniListArtist"
                                android:layout_width="match_parent"
                                android:layout_height="wrap_content"
                                android:text="الرسام: --"
                                android:textColor="?attr/colorOnSurface"
                                android:textSize="13sp"
                                android:layout_marginBottom="4dp"/>
                            <TextView
                                android:id="@+id/tvAniListCountry"
                                android:layout_width="match_parent"
                                android:layout_height="wrap_content"
                                android:text="المنشأ: --"
                                android:textColor="?attr/colorOnSurface"
                                android:textSize="13sp"
                                android:layout_marginBottom="4dp"/>
                            <TextView
                                android:id="@+id/tvAniListDates"
                                android:layout_width="match_parent"
                                android:layout_height="wrap_content"
                                android:text="تاريخ الإصدار: --"
                                android:textColor="?attr/colorOnSurface"
                                android:textSize="13sp"/>
                        </LinearLayout>
                    </com.google.android.material.card.MaterialCardView>

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginHorizontal="24dp"
                        android:layout_marginTop="24dp"
                        android:text="القصة"
                        android:textColor="?attr/colorOnSurface"
                        android:textSize="18sp"
                        android:textStyle="bold" />

                    <TextView
                        android:id="@+id/mangaDescription"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:layout_marginHorizontal="24dp"
                        android:layout_marginTop="12dp"
                        android:layout_marginBottom="40dp"
                        android:lineSpacingExtra="8dp"
                        android:textColor="?attr/colorOnSurfaceVariant"
                        android:textSize="15sp" />
                </LinearLayout>

                <!-- Chapters Container -->
                <androidx.swiperefreshlayout.widget.SwipeRefreshLayout
                    android:id="@+id/swipeRefreshLayout"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:visibility="gone">
                    <androidx.recyclerview.widget.RecyclerView
                        android:id="@+id/chaptersRecyclerView"
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:clipToPadding="false"
                        android:paddingBottom="40dp" />
                </androidx.swiperefreshlayout.widget.SwipeRefreshLayout>

                <ProgressBar
                    android:id="@+id/progressBar"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_gravity="center_horizontal"
                    android:layout_marginTop="40dp"
                    android:indeterminateTint="?attr/colorPrimary" />
            </FrameLayout>
        </LinearLayout>
    </androidx.core.widget.NestedScrollView>

    <!-- Invisible elements to prevent crash in old logic -->
    <TextView android:id="@+id/mangaStatus" android:layout_width="0dp" android:layout_height="0dp" android:visibility="gone"/>
    <TextView android:id="@+id/tvAniListFormat" android:layout_width="0dp" android:layout_height="0dp" android:visibility="gone"/>

</androidx.coordinatorlayout.widget.CoordinatorLayout>
"""

with open('app/src/main/res/layout/activity_manga_detail.xml', 'w') as f:
    f.write(xml_content)
