import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

# Add variables to class
class_vars = """
    private android.widget.ImageView btnFavorite;
    private android.widget.ImageView btnNotification;
    private boolean isSubscribedToNotifications = false;
"""
content = re.sub(r'private android\.widget\.ImageView btnFavorite;(?![\s\S]*isSubscribedToNotifications)', class_vars, content, count=1)
# Some places we had ImageView btnFavorite; let's just make sure.
content = content.replace("private ImageView btnFavorite;", class_vars)

# Fix duplicate btnNotification
content = re.sub(r'private ImageView btnNotification;\n    private boolean isSubscribedToNotifications = false;\n', '', content)

# Remove the rvChapters and statsContainer visibility errors
# Let's see what happened to rvChapters and statsContainer.
# Let's just remove the bad lines.
bad_lines = [
    "rvChapters.setVisibility(View.GONE);",
    "statsContainer.setVisibility(View.GONE);",
    "rvChapters.setVisibility(View.VISIBLE);",
    "statsContainer.setVisibility(View.VISIBLE);",
    "setupRadarChart();",
    "btnNotification = findViewById(R.id.btnNotification);"
]

# Wait, `rvChapters` might be legitimate in some places? No, rvChapters is not in the file?
# Actually, the error says: cannot find symbol variable rvChapters

# Let's inspect MangaDetailActivity first.
