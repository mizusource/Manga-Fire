with open('app/src/main/res/layout/item_manga.xml', 'w') as f:
    f.write('''<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/mangaCard"
    android:layout_width="match_parent"
    android:layout_height="240dp"
    android:layout_margin="6dp"
    app:cardBackgroundColor="@android:color/transparent"
    app:cardCornerRadius="16dp"
    app:cardElevation="0dp"
    app:strokeWidth="0dp"
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

        <!-- Gradient overlay to make text readable -->
        <View
            android:layout_width="match_parent"
            android:layout_height="120dp"
            android:background="@drawable/gradient_bottom"
            app:layout_constraintBottom_toBottomOf="parent" />

        <View
            android:layout_width="match_parent"
            android:layout_height="60dp"
            android:background="@drawable/bg_gradient_top_to_bottom"
            app:layout_constraintTop_toTopOf="parent" />

        <!-- Status indicator (floating badge) -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:cardCornerRadius="12dp"
            app:cardBackgroundColor="#CC000000"
            app:strokeWidth="1dp"
            app:strokeColor="?attr/colorPrimary"
            android:layout_margin="8dp"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintStart_toStartOf="parent">

            <TextView
                android:id="@+id/mangaStatus"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:paddingHorizontal="8dp"
                android:paddingVertical="4dp"
                android:textColor="?attr/colorPrimary"
                android:textSize="10sp"
                android:textStyle="bold" />
        </com.google.android.material.card.MaterialCardView>

        <!-- Chapter indicator (floating badge) -->
        <com.google.android.material.card.MaterialCardView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:cardCornerRadius="8dp"
            app:cardBackgroundColor="#99000000"
            android:layout_margin="8dp"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintEnd_toEndOf="parent">

            <TextView
                android:id="@+id/mangaChapter"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:paddingHorizontal="6dp"
                android:paddingVertical="4dp"
                android:textColor="#FFFFFF"
                android:textSize="10sp"
                android:textStyle="bold" />
        </com.google.android.material.card.MaterialCardView>

        <!-- Title Text container -->
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:padding="12dp"
            app:layout_constraintBottom_toBottomOf="parent">
            
            <TextView
                android:id="@+id/mangaTitle"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="@android:color/white"
                android:textSize="15sp"
                android:textStyle="bold"
                android:maxLines="2"
                android:ellipsize="end"
                android:shadowColor="#000000"
                android:shadowDx="1"
                android:shadowDy="1"
                android:shadowRadius="3" />
        </LinearLayout>
        
    </androidx.constraintlayout.widget.ConstraintLayout>
</com.google.android.material.card.MaterialCardView>
''')
