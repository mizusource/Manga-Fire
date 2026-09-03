import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchScreen.kt'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('Color.Transparent', 'androidx.compose.ui.graphics.Color.Transparent')

with open(filepath, 'w') as f:
    f.write(content)
