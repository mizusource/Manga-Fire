import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/SearchActivity.java", "r") as f:
    text = f.read()

pattern = r'(SearchFilterDialog\.show\(SearchActivity\.this, currentQuery, request -> \{.*?\}\);)'

replacement = r'''\1'''

# Make sure performSearch handles it
# It looks like performSearch only takes String query. Let's see it.

