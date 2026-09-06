import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

content = re.sub(r'if \(tvFavorite == null\) \{[\s\S]*?tvFavorite = tvDropped; // Fallback\n            \}', '', content)
content = content.replace("else if (currentLibraryStatus.equals(\"favorite\")) tvFavorite.setTextColor(android.graphics.Color.parseColor(\"#E91E63\"));", "")

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)
