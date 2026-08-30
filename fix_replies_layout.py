with open('app/src/main/res/layout/activity_replies.xml', 'r') as f:
    content = f.read()

content = content.replace('@drawable/ic_arrow_back', '@drawable/ic_back_arrow')

content = content.replace("""        <EditText
            android:id="@+id/etReply"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:background="@drawable/rounded_edittext_bg"
            android:hint="اكتب ردك..."
            android:padding="12dp"
            android:textColor="?attr/colorOnSurface"
            android:textColorHint="?attr/colorOnSurfaceVariant"
            android:textSize="15sp"
            android:maxLines="4"
            android:inputType="textMultiLine" />""",
"""        <com.google.android.material.textfield.TextInputLayout
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:layout_marginEnd="8dp"
            app:boxBackgroundColor="?attr/colorSurface"
            app:boxCornerRadiusTopStart="24dp"
            app:boxCornerRadiusTopEnd="24dp"
            app:boxCornerRadiusBottomStart="24dp"
            app:boxCornerRadiusBottomEnd="24dp"
            app:boxStrokeWidth="0dp">

            <com.google.android.material.textfield.TextInputEditText
                android:id="@+id/etReply"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:background="@null"
                android:hint="اكتب ردك..."
                android:textColorHint="?attr/colorOnSurfaceVariant"
                android:textColor="?attr/colorOnSurface"
                android:textSize="14sp"
                android:maxLines="4"
                android:inputType="textMultiLine" />
        </com.google.android.material.textfield.TextInputLayout>""")

content = content.replace("""        <ImageButton
            android:id="@+id/btnSend"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:layout_marginStart="8dp"
            android:background="@drawable/circle_bg"
            android:backgroundTint="?attr/colorPrimary"
            android:src="@drawable/ic_send"
            app:tint="?attr/colorOnPrimary" />""",
"""        <com.google.android.material.floatingactionbutton.FloatingActionButton
            android:id="@+id/btnSend"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:fabSize="mini"
            android:src="@android:drawable/ic_menu_send"
            app:tint="#FFFFFF"
            app:backgroundTint="?attr/colorPrimary"
            app:elevation="0dp" />""")

with open('app/src/main/res/layout/activity_replies.xml', 'w') as f:
    f.write(content)

