import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

pattern = r'private void showRatingStatsDialog\(\)\s*\{.*?\s*\}\s*\}\s*\}\s*private void toggleFavorite\(\)'

def replacer(match):
    # we just need to drop the extra braces before `private void toggleFavorite()`
    text = match.group(0)
    # find toggleFavorite and replace all the trailing `}` with a single `}`
    text = re.sub(r'\}\s*\}\s*\}\s*private void toggleFavorite\(\)', '    }\n\n    private void toggleFavorite()', text)
    return text

new_content = re.sub(pattern, replacer, content, flags=re.DOTALL)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(new_content)
print("Regex replace done")
