with open('app/src/main/java/com/fire/mangareader/activity/ProfileActivity.java', 'r') as f:
    content = f.read()

# Replace button logic
# I will use a regex or just simple string replacement

# Find the btnSettings logic
content = content.replace('ImageView btnSettings = findViewById(R.id.btnSettings);', 'ImageView btnSettings = findViewById(R.id.btnSettings);\n        ImageView btnAdmin = findViewById(R.id.btnAdmin);\n        com.google.android.material.button.MaterialButton btnEditProfile = findViewById(R.id.btnEditProfile);')

# Replace the click listeners for non-logged in
old_not_logged = """btnSettings.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });"""
new_not_logged = """btnSettings.setOnClickListener(v -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            });
            btnAdmin.setVisibility(View.GONE);
            btnEditProfile.setVisibility(View.GONE);"""
content = content.replace(old_not_logged, new_not_logged)

# Replace the click listeners for logged in
old_logged = """btnSettings.setOnClickListener(v -> showEditProfileDialog());"""
new_logged = """btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
            btnAdmin.setVisibility(View.VISIBLE);
            btnAdmin.setOnClickListener(v -> startActivity(new Intent(this, AdminDashboardActivity.class)));
            btnEditProfile.setOnClickListener(v -> showEditProfileDialog());"""
content = content.replace(old_logged, new_logged)

with open('app/src/main/java/com/fire/mangareader/activity/ProfileActivity.java', 'w') as f:
    f.write(content)
