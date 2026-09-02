import re

filepath = 'app/src/main/res/layout/activity_search.xml'
with open(filepath, 'r') as f:
    content = f.read()

# Make search bar glassmorphic and softer
content = content.replace('app:cardBackgroundColor="?attr/colorSurface"', 'app:cardBackgroundColor="#1AFFFFFF"\n        app:strokeColor="#33FFFFFF"\n        app:strokeWidth="1dp"')

with open(filepath, 'w') as f:
    f.write(content)
print("Updated search xml")
