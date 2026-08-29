import os

login_xml = """<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout 
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layoutDirection="rtl"
    android:background="?android:attr/colorBackground">
    
    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/bg_appbar"
        android:scaleType="centerCrop"
        android:alpha="0.2" />
        
    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:fillViewport="true">
        
        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:orientation="vertical"
            android:gravity="center"
            android:padding="24dp">
            
            <com.google.android.material.card.MaterialCardView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:cardBackgroundColor="?attr/colorSurfaceVariant"
                app:cardCornerRadius="24dp"
                app:cardElevation="0dp"
                app:strokeWidth="1dp"
                app:strokeColor="?attr/colorOutline">
                
                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:orientation="vertical"
                    android:padding="24dp"
                    android:gravity="center">
                    
                    <ImageView
                        android:layout_width="80dp"
                        android:layout_height="80dp"
                        android:src="@mipmap/ic_launcher"
                        android:layout_marginBottom="16dp"/>
                        
                    <TextView
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="تسجيل الدخول"
                        android:textSize="24sp"
                        android:textStyle="bold"
                        android:textColor="?attr/colorOnSurface"
                        android:layout_marginBottom="32dp"/>
                        
                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="البريد الإلكتروني"
                        app:startIconDrawable="@android:drawable/ic_dialog_email"
                        android:layout_marginBottom="16dp"
                        style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
                        app:boxCornerRadiusTopStart="16dp"
                        app:boxCornerRadiusTopEnd="16dp"
                        app:boxCornerRadiusBottomStart="16dp"
                        app:boxCornerRadiusBottomEnd="16dp"
                        app:boxStrokeColor="?attr/colorPrimary">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/etEmail"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textEmailAddress" />
                    </com.google.android.material.textfield.TextInputLayout>
                    
                    <com.google.android.material.textfield.TextInputLayout
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:hint="كلمة المرور"
                        app:startIconDrawable="@android:drawable/ic_lock_idle_lock"
                        app:endIconMode="password_toggle"
                        android:layout_marginBottom="24dp"
                        style="@style/Widget.Material3.TextInputLayout.OutlinedBox"
                        app:boxCornerRadiusTopStart="16dp"
                        app:boxCornerRadiusTopEnd="16dp"
                        app:boxCornerRadiusBottomStart="16dp"
                        app:boxCornerRadiusBottomEnd="16dp"
                        app:boxStrokeColor="?attr/colorPrimary">
                        <com.google.android.material.textfield.TextInputEditText
                            android:id="@+id/etPassword"
                            android:layout_width="match_parent"
                            android:layout_height="wrap_content"
                            android:inputType="textPassword" />
                    </com.google.android.material.textfield.TextInputLayout>
                    
                    <com.google.android.material.button.MaterialButton
                        android:id="@+id/btnLogin"
                        android:layout_width="match_parent"
                        android:layout_height="56dp"
                        android:text="تسجيل الدخول"
                        android:textSize="18sp"
                        android:textStyle="bold"
                        app:cornerRadius="16dp"
                        app:backgroundTint="?attr/colorPrimary"
                        android:layout_marginBottom="16dp"/>
                        
                    <com.google.android.material.button.MaterialButton
                        android:id="@+id/btnGoToRegister"
                        android:layout_width="match_parent"
                        android:layout_height="wrap_content"
                        android:text="إنشاء حساب جديد"
                        style="@style/Widget.Material3.Button.TextButton" />
                </LinearLayout>
            </com.google.android.material.card.MaterialCardView>
        </LinearLayout>
    </ScrollView>
</androidx.coordinatorlayout.widget.CoordinatorLayout>
"""

with open('app/src/main/res/layout/activity_login.xml', 'w') as f:
    f.write(login_xml)

register_xml = login_xml.replace("تسجيل الدخول", "إنشاء حساب").replace("btnLogin", "btnRegister").replace("btnGoToRegister", "btnGoToLogin").replace("إنشاء حساب جديد", "لدي حساب بالفعل")

with open('app/src/main/res/layout/activity_register.xml', 'w') as f:
    f.write(register_xml)

