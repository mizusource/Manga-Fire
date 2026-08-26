import re

with open('app/src/main/java/com/fire/mangareader/activity/LoginActivity.java', 'r') as f:
    content = f.read()

imports = """
import android.net.Uri;
import com.google.firebase.auth.ActionCodeSettings;
import android.content.SharedPreferences;
"""
content = content.replace('import com.google.firebase.auth.FirebaseUser;', 'import com.google.firebase.auth.FirebaseUser;' + imports)

vars_replacement = """
    private Button btnLogin, btnMagicLink;
"""
content = content.replace('private Button btnLogin;', vars_replacement)

init_replacement = """
        btnLogin = findViewById(R.id.btnLogin);
        btnMagicLink = findViewById(R.id.btnMagicLink);
        
        btnMagicLink.setOnClickListener(v -> sendMagicLink());
        
        checkEmailLink();
"""
content = content.replace('btnLogin = findViewById(R.id.btnLogin);', init_replacement)

methods = """
    private void sendMagicLink() {
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "يرجى إدخال البريد الإلكتروني لإرسال الرابط", Toast.LENGTH_SHORT).show();
            return;
        }
        
        progressBar.setVisibility(View.VISIBLE);
        btnMagicLink.setEnabled(false);
        
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl("https://mangafire.page.link/login")
                .setHandleCodeInApp(true)
                .setAndroidPackageName(getPackageName(), true, "12")
                .build();
                
        mAuth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    btnMagicLink.setEnabled(true);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "تم إرسال رابط الدخول إلى بريدك الإلكتروني", Toast.LENGTH_LONG).show();
                        SharedPreferences prefs = getSharedPreferences("MagicLinkPrefs", MODE_PRIVATE);
                        prefs.edit().putString("email", email).apply();
                    } else {
                        Toast.makeText(this, "خطأ: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
    
    private void checkEmailLink() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null) {
            String emailLink = intent.getData().toString();
            if (mAuth.isSignInWithEmailLink(emailLink)) {
                SharedPreferences prefs = getSharedPreferences("MagicLinkPrefs", MODE_PRIVATE);
                String email = prefs.getString("email", "");
                
                if (email.isEmpty()) {
                    Toast.makeText(this, "تعذر العثور على البريد الإلكتروني، يرجى المحاولة مجدداً", Toast.LENGTH_LONG).show();
                    return;
                }
                
                progressBar.setVisibility(View.VISIBLE);
                mAuth.signInWithEmailLink(email, emailLink)
                        .addOnCompleteListener(task -> {
                            progressBar.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    PreferenceManager prefManager = new PreferenceManager(this);
                                    prefManager.saveUser(user.getUid(), user.getEmail(),
                                            user.getDisplayName() != null ? user.getDisplayName() : user.getEmail(), false);
                                    Toast.makeText(this, "تم تسجيل الدخول بنجاح عبر الرابط السحري", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(this, MainActivity.class));
                                    finish();
                                }
                            } else {
                                Toast.makeText(this, "فشل الدخول بالرابط: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        }
    }
"""

content = content.replace('private void continueAsGuest() {', methods + '\n    private void continueAsGuest() {')

with open('app/src/main/java/com/fire/mangareader/activity/LoginActivity.java', 'w') as f:
    f.write(content)

