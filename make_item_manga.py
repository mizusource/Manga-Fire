xml_content = """<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/mangaCard"
    android:layout_width="match_parent"
    android:layout_height="220dp"
    android:layout_margin="8dp"
    app:cardBackgroundColor="?attr/colorSurfaceVariant"
    app:cardCornerRadius="16dp"
    app:cardElevation="4dp"
    app:strokeWidth="1dp"
    app:strokeColor="#1AFFFFFF"
    android:clickable="true"
    android:focusable="true"
    android:foreground="?attr/selectableItemBackground">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <ImageView
            android:id="@+id/mangaCover"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:scaleType="centerCrop" />

        <!-- Deep gradient overlay for text readability -->
        <View
            android:layout_width="match_parent"
            android:layout_height="140dp"
            android:background="@drawable/gradient_bottom"
            app:layout_constraintBottom_toBottomOf="parent" />

        <View
            android:layout_width="match_parent"
            android:layout_height="60dp"
            android:background="@drawable/bg_gradient_top_to_bottom"
            app:layout_constraintTop_toTopOf="parent" />

        <!-- Status / Rating Badge (Sleek Glass Pill) -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:cardCornerRadius="10dp"
            app:cardBackgroundColor="#CC000000"
            app:strokeWidth="1dp"
            app:strokeColor="#33FFFFFF"
            android:layout_margin="10dp"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent">
            
            <LinearLayout
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:gravity="center_vertical"
                android:paddingHorizontal="8dp"
                android:paddingVertical="4dp">
                
                <TextView
                    android:id="@+id/mangaStatus"
                    android:layout_width="wrap_content"
                    android:layout_height="wrap_content"
                    android:textColor="#FFC107"
                    android:textSize="11sp"
                    android:textStyle="bold" />
            </LinearLayout>
        </com.google.android.material.card.MaterialCardView>

        <!-- Chapter indicator -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:cardCornerRadius="10dp"
            app:cardBackgroundColor="#CC000000"
            app:strokeWidth="1dp"
            app:strokeColor="#33FFFFFF"
            android:layout_margin="10dp"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintEnd_toEndOf="parent">
            
            <TextView
                android:id="@+id/mangaChapter"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:paddingHorizontal="8dp"
                android:paddingVertical="4dp"
                android:textColor="#FFFFFF"
                android:textSize="11sp"
                android:textStyle="bold" />
        </com.google.android.material.card.MaterialCardView>

        <!-- Title Text container -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:paddingHorizontal="12dp"
            android:paddingBottom="12dp"
            app:layout_constraintBottom_toBottomOf="parent">
            
            <TextView
                android:id="@+id/mangaTitle"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="@android:color/white"
                android:textSize="14sp"
                android:textStyle="bold"
                android:maxLines="2"
                android:ellipsize="end"
                android:shadowColor="#000000"
                android:shadowDx="1"
                android:shadowDy="1"
                android:shadowRadius="5"
                android:layout_marginBottom="4dp" />
                
        </LinearLayout>

    </androidx.constraintlayout.widget.ConstraintLayout>
</com.google.android.material.card.MaterialCardView>
"""

with open('app/src/main/res/layout/item_manga.xml', 'w') as f:
    f.write(xml_content)
