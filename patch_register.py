import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/RegisterActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Add import if not present
if "UserAuthValidator" not in content:
    content = content.replace('import com.fire.mangareader.data.network.SupabaseManager;',
                              'import com.fire.mangareader.data.network.SupabaseManager;\nimport com.fire.mangareader.domain.usecase.auth.UserAuthValidator;\n')

# Find registerUser()
old_register = """    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }"""

new_register = """    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // New Advanced Domain Validator
        UserAuthValidator.ValidationResult result = UserAuthValidator.validateSignUp(name, email, password, password); // We don't have confirm password field yet, passing password twice to skip that check
        if (!result.isValid) {
            Toast.makeText(this, result.errorMessage, Toast.LENGTH_LONG).show();
            return;
        }"""

content = content.replace(old_register, new_register)

with open(filepath, 'w') as f:
    f.write(content)
print("Patched RegisterActivity")
