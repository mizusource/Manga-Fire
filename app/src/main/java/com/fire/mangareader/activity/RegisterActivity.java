package com.fire.mangareader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fire.mangareader.R;
import com.fire.mangareader.utils.PreferenceManager;
import com.fire.mangareader.network.SupabaseManager;

public class RegisterActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPassword;
    private Button btnRegister, btnGoToLogin;
    private SupabaseManager supabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.utils.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        supabaseManager = SupabaseManager.getInstance(this);
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnGoToLogin = findViewById(R.id.btnGoToLogin);

        btnRegister.setOnClickListener(v -> registerUser());
        btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRegister.setEnabled(false);

        btnRegister.setEnabled(false);
        supabaseManager.signUp(email, password, new SupabaseManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, "تم التسجيل بنجاح، يرجى تسجيل الدخول", Toast.LENGTH_SHORT).show();
                // Normally supabase might require email verification, but assuming it works directly or redirects to login
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finishAffinity();
            }

            @Override
            public void onError(String error) {
                btnRegister.setEnabled(true);
                Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
