package com.fire.mangareader.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.fire.mangareader.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AdminLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        TextInputEditText etAdminUsername = findViewById(R.id.etAdminUsername);
        TextInputEditText etAdminPassword = findViewById(R.id.etAdminPassword);
        MaterialButton btnAdminLogin = findViewById(R.id.btnAdminLogin);

        btnAdminLogin.setOnClickListener(v -> {
            String username = etAdminUsername.getText() != null ? etAdminUsername.getText().toString().trim() : "";
            String password = etAdminPassword.getText() != null ? etAdminPassword.getText().toString().trim() : "";

            // التحقق من بيانات المدير
            if ((username.equalsIgnoreCase("admin") || username.equalsIgnoreCase("mstfybdwy633@gmail.com")) 
                    && password.equals("admin123")) {
                
                Toast.makeText(this, "تم تسجيل الدخول كمسؤول بنجاح", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AdminLoginActivity.this, AdminDashboardActivity.class));
                finish();
            } else {
                Toast.makeText(this, "اسم المستخدم أو كلمة المرور غير صحيحة", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
