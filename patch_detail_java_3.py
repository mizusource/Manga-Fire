import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Fix tvUserRating usage
content = re.sub(r'tvUserRating\.setText\("-/10"\);', r'// tvUserRating', content)
content = re.sub(r'tvUserRating\.setText\(ratings\[which\]\.split\(" "\)\[0\]\);', r'// tvUserRating', content)

# Fix btnMyList usage
content = re.sub(r'btnMyList\.setOnClickListener\(v -> showMyListBottomSheet\(\)\);', r'// btnMyList', content)

# Fix tvMyListStatus usage
content = re.sub(r'tvMyListStatus\.setText\(status\);', r'// tvMyListStatus', content)
content = re.sub(r'tvMyListStatus\.setTextColor', r'// tvMyListStatus', content)

# Fix AniList fields usage
content = re.sub(r'tvAniListFormat\.setText', r'// tvAniListFormat', content)
content = re.sub(r'tvAniListAuthor\.setText', r'// tvAniListAuthor', content)
content = re.sub(r'tvAniListArtist\.setText', r'// tvAniListArtist', content)
content = re.sub(r'tvAniListDates\.setText', r'// tvAniListDates', content)
content = re.sub(r'if \(false && tvAniListAuthor\.getText\(\)\.toString\(\)\.contains', r'// if', content)


with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

