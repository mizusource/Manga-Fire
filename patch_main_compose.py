import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt'
with open(filepath, 'r') as f:
    content = f.read()

# Replace the Unresolved currentBackStateEntryAsState call.
content = content.replace('import androidx.navigation.compose.currentBackStateEntryAsState', 'import androidx.navigation.compose.currentBackStackEntryAsState')
content = content.replace('currentBackStateEntryAsState', 'currentBackStackEntryAsState')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched MainComposeActivity.kt")
