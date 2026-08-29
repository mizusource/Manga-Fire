import re

with open('app/src/main/java/com/fire/mangareader/activity/SettingsActivity.java', 'r') as f:
    content = f.read()

toolbar_logic = """
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
"""

if "findViewById(R.id.toolbar)" not in content:
    content = content.replace(
        "setContentView(R.layout.activity_settings);",
        "setContentView(R.layout.activity_settings);\n" + toolbar_logic
    )

with open('app/src/main/java/com/fire/mangareader/activity/SettingsActivity.java', 'w') as f:
    f.write(content)
