cat << 'INNER' > app/src/main/res/layout/item_manga_grid.xml
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="6dp"
    app:cardBackgroundColor="?attr/colorSurface"
    app:cardCornerRadius="12dp"
    app:cardElevation="2dp"
    app:strokeWidth="0dp"
    android:clickable="true"
    android:focusable="true"
    android:foreground="?attr/selectableItemBackground">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="200dp">

            <!-- Cover Image -->
            <ImageView
                android:id="@+id/mangaCover"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:scaleType="centerCrop" />

            <!-- Gradient Overlay (Optional for better text visibility if needed) -->
            <View
                android:layout_width="match_parent"
                android:layout_height="60dp"
                android:background="@drawable/gradient_bottom"
                app:layout_constraintBottom_toBottomOf="parent" />

            <!-- Latest Chapter Badge -->
            <com.google.android.material.card.MaterialCardView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                app:cardCornerRadius="4dp"
                app:cardBackgroundColor="?attr/colorSurface"
                android:layout_margin="8dp"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent">
                
                <TextView
                    android:id="@+id/tvLatestChapter"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:paddingHorizontal="6dp"
                    android:paddingVertical="2dp"
                    android:text="Ch. 150"
                    android:textColor="?attr/colorOnSurface"
                    android:textSize="11sp"
                    android:textStyle="bold" />
            </com.google.android.material.card.MaterialCardView>

        </androidx.constraintlayout.widget.ConstraintLayout>

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="8dp">

            <!-- Title -->
            <TextView
                android:id="@+id/tvMangaTitle"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:ellipsize="end"
                android:maxLines="1"
                android:textColor="?attr/colorOnSurface"
                android:textSize="13sp"
                android:textStyle="bold" />

            <RelativeLayout
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_marginTop="4dp">

                <LinearLayout
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:orientation="horizontal"
                    android:gravity="center_vertical"
                    android:layout_alignParentStart="true">
                    
                    <TextView
                        android:id="@+id/tvRating"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="5.0"
                        android:textColor="#FBC02D"
                        android:textSize="11sp"
                        android:textStyle="bold" />
                    
                    <ImageView
                        android:layout_width="10dp"
                        android:layout_height="10dp"
                        android:layout_marginStart="2dp"
                        android:src="@android:drawable/star_on"
                        app:tint="#FBC02D" />
                </LinearLayout>

                <TextView
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:layout_alignParentEnd="true"
                    android:text="مستمرة"
                    android:textColor="?attr/colorOnSurfaceVariant"
                    android:textSize="10sp" />
            </RelativeLayout>
        </LinearLayout>
    </LinearLayout>
</com.google.android.material.card.MaterialCardView>
INNER
