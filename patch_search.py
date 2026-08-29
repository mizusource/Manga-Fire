with open('app/src/main/res/layout/activity_search.xml', 'r') as f:
    content = f.read()

content = content.replace(
    'app:cardBackgroundColor="?attr/colorSurface"\n        app:cardCornerRadius="28dp"\n        app:cardElevation="8dp"\n        app:strokeWidth="0dp"',
    'app:cardBackgroundColor="?attr/colorSurfaceVariant"\n        app:cardCornerRadius="28dp"\n        app:cardElevation="0dp"\n        app:strokeColor="?attr/colorOutline"\n        app:strokeWidth="1dp"'
)

with open('app/src/main/res/layout/activity_search.xml', 'w') as f:
    f.write(content)
