with open('app/src/main/res/layout/activity_register.xml', 'r') as f:
    content = f.read()

name_field = """
                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="الاسم"
                        app:startIconDrawable="@android:drawable/ic_menu_edit"
                        android:layout_marginBottom="16dp"
                        style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
                        app:boxCornerRadiusTopStart="16dp"
                        app:boxCornerRadiusTopEnd="16dp"
                        app:boxCornerRadiusBottomStart="16dp"
                        app:boxCornerRadiusBottomEnd="16dp"
                        app:boxStrokeColor="?attr/colorPrimary">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/etName"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textPersonName" />
                    </com.google.android.material.textfield.TextInputLayout>
"""

content = content.replace(
    '<com.google.android.material.textfield.TextInputLayout\n                        android:layout_width="match_parent"\n                        android:layout_height="wrap_content"\n                        android:hint="البريد الإلكتروني"',
    name_field + '\n                    <com.google.android.material.textfield.TextInputLayout\n                        android:layout_width="match_parent"\n                        android:layout_height="wrap_content"\n                        android:hint="البريد الإلكتروني"'
)

with open('app/src/main/res/layout/activity_register.xml', 'w') as f:
    f.write(content)
