with open('app/src/main/java/com/fire/mangareader/activity/LoginActivity.java', 'r') as f:
    content = f.read()

import re

# Add btnForgotPassword field
content = content.replace('private Button btnLogin, btnGoToRegister;', 'private Button btnLogin, btnGoToRegister, btnForgotPassword;')

# Initialize btnForgotPassword and set click listener
init_code = """
        btnGoToRegister = findViewById(R.id.btnGoToRegister);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
        
        btnForgotPassword.setOnClickListener(v -> handleForgotPassword());
"""
content = content.replace('btnGoToRegister = findViewById(R.id.btnGoToRegister);', init_code.strip())

# Add handleForgotPassword method
forgot_method = """
    private void handleForgotPassword() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "يرجى كتابة بريدك الإلكتروني في الحقل المخصص أولاً", Toast.LENGTH_SHORT).show();
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
"""

content = content.replace('private void loginUser() {', forgot_method + '\n    private void loginUser() {')

with open('app/src/main/java/com/fire/mangareader/activity/LoginActivity.java', 'w') as f:
    f.write(content)
