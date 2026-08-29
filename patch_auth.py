import re

# ------------- Patch LoginActivity -------------
with open('app/src/main/java/com/fire/mangareader/activity/LoginActivity.java', 'r') as f:
    content = f.read()

# Replace imports
content = re.sub(r'import com.google.firebase.auth.FirebaseAuth;.*?import com.google.firebase.auth.FirebaseUser;', 'import com.fire.mangareader.network.SupabaseManager;', content, flags=re.DOTALL)

# Replace variables
content = content.replace('private FirebaseAuth mAuth;', 'private SupabaseManager supabaseManager;')

# Replace onCreate initialization
content = content.replace('mAuth = FirebaseAuth.getInstance();', 'supabaseManager = SupabaseManager.getInstance(this);')

# Replace loginUser logic
login_logic = """
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
"""

# Find the old login logic and replace it
# mAuth.signInWithEmailAndPassword(email, password)...
content = re.sub(r'mAuth\.signInWithEmailAndPassword\(.*?\)\s*\.addOnCompleteListener.*?\}\s*\);\s*\}', login_logic.strip() + '\n    }', content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/activity/LoginActivity.java', 'w') as f:
    f.write(content)

# ------------- Patch RegisterActivity -------------
with open('app/src/main/java/com/fire/mangareader/activity/RegisterActivity.java', 'r') as f:
    content = f.read()

# Replace imports
content = re.sub(r'import com.google.firebase.auth.FirebaseAuth;.*?import com.google.firebase.auth.UserProfileChangeRequest;', 'import com.fire.mangareader.network.SupabaseManager;', content, flags=re.DOTALL)

# Replace variables
content = content.replace('private FirebaseAuth mAuth;', 'private SupabaseManager supabaseManager;')

# Replace onCreate initialization
content = content.replace('mAuth = FirebaseAuth.getInstance();', 'supabaseManager = SupabaseManager.getInstance(this);')

# Replace registerUser logic
register_logic = """
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
"""

content = re.sub(r'mAuth\.createUserWithEmailAndPassword\(.*?\)\s*\.addOnCompleteListener.*?\}\s*\);\s*\}', register_logic.strip() + '\n    }', content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/activity/RegisterActivity.java', 'w') as f:
    f.write(content)

