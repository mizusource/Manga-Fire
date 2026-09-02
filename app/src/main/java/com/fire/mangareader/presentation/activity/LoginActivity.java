package com.fire.mangareader.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.fire.mangareader.R;
import com.fire.mangareader.util.PreferenceManager;
import com.fire.mangareader.data.network.SupabaseManager;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnGoToRegister, btnForgotPassword;
    private SupabaseManager supabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        com.fire.mangareader.util.ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        supabaseManager = SupabaseManager.getInstance(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        
        btnForgotPassword.setOnClickListener(v -> handleForgotPassword());

        btnLogin.setOnClickListener(v -> loginUser());
        btnGoToRegister.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));
    }

    
    private void handleForgotPassword() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "يرجى كتابة بريدك الإلكتروني في الحقل المخصص أولاً", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!com.fire.mangareader.util.ValidationUtils.isValidEmail(email)) {
            Toast.makeText(this, "يرجى إدخال بريد إلكتروني صالح", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnForgotPassword.setEnabled(false);
        supabaseManager.resetPassword(email, new SupabaseManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                runOnUiThread(() -> {
                    btnForgotPassword.setEnabled(true);
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    btnForgotPassword.setEnabled(true);
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "يرجى تعبئة جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!com.fire.mangareader.util.ValidationUtils.isValidEmail(email)) {
            Toast.makeText(this, "يرجى إدخال بريد إلكتروني صالح", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);

        btnLogin.setEnabled(false);
        supabaseManager.signIn(email, password, new SupabaseManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                btnLogin.setEnabled(true);
                PreferenceManager prefs = new PreferenceManager(LoginActivity.this);
                // Save user info, name defaults to email initially, we can fetch profile later if needed
                prefs.saveUser(supabaseManager.getCurrentUserId(), email, email, false);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
    }
}
