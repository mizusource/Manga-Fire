import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/SettingsActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

# Add listener for btnBackSettings
target = """        if (savedInstanceState == null) {
            getSupportFragmentManager()"""
replacement = """        android.view.View btnBack = findViewById(R.id.btnBackSettings);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (savedInstanceState == null) {"""

content = content.replace(target, replacement)

with open(filepath, 'w') as f:
    f.write(content)
print("Added back button to SettingsActivity")
