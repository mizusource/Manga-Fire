import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Replace missing variables and IDs with safe alternatives or remove them
content = re.sub(r'TextView mangaTitleDetail = findViewById\(R\.id\.mangaTitleDetail\);', '', content)
content = re.sub(r'if \(mangaTitleDetail != null\) mangaTitleDetail\.setText\(.*?\);', '', content)

content = re.sub(r'btnFavorite = findViewById\(R\.id\.btnFavorite\);', '', content)
content = re.sub(r'btnFavoriteContainer = findViewById\(R\.id\.btnFavoriteContainer\);', '', content)
content = re.sub(r'btnCommentsContainer = findViewById\(R\.id\.btnCommentsContainer\);', '', content)
content = re.sub(r'tvFavoriteText = findViewById\(R\.id\.tvFavoriteText\);', '', content)

# MyList -> btnChangeStatus
content = re.sub(r'View btnMyList = findViewById\(R\.id\.btnMyList\);', 'View btnMyList = findViewById(R.id.btnChangeStatus);', content)
content = re.sub(r'android\.widget\.TextView tvMyListStatus = findViewById\(R\.id\.tvMyListStatus\);', 'android.widget.TextView tvMyListStatus = findViewById(R.id.btnChangeStatus).findViewById(R.id.tvFavoriteText); // dummy', content)

# Ratings
content = re.sub(r'TextView tvGlobalRatingCount = findViewById\(R\.id\.tvGlobalRatingCount\);', 'TextView tvGlobalRatingCount = null;', content)
content = re.sub(r'TextView tvALRating = findViewById\(R\.id\.tvALRating\);', 'TextView tvALRating = findViewById(R.id.tvAniListScore);', content)
content = re.sub(r'TextView tvALRatingCount = findViewById\(R\.id\.tvALRatingCount\);', 'TextView tvALRatingCount = null;', content)

content = re.sub(r'View btnUserRating = findViewById\(R\.id\.btnUserRating\);', 'View btnUserRating = findViewById(R.id.btnRate);', content)
content = re.sub(r'ImageView ivUserRatingStar = findViewById\(R\.id\.ivUserRatingStar\);', 'ImageView ivUserRatingStar = null;', content)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)
