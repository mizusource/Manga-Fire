import re

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'r') as f:
    content = f.read()

# Fix chapterList
content = content.replace('chaptersList', 'chapterList')

# Fix tvUserRating
content = re.sub(r'TextView tvUserRating = findViewById\(R\.id\.tvUserRating\);', r'// TextView tvUserRating = findViewById(R.id.tvUserRating);', content)
content = re.sub(r'if \(tvUserRating != null\)', r'if (false)', content)

# Fix btnMyList
content = re.sub(r'View btnMyList = findViewById\(R\.id\.btnMyList\);', r'// View btnMyList', content)
content = re.sub(r'if \(btnMyList != null\)', r'if (false)', content)

# Fix tvMyListStatus
content = re.sub(r'android\.widget\.TextView tvMyListStatus = findViewById\(R\.id\.tvMyListStatus\);', r'// android.widget.TextView tvMyListStatus', content)
content = re.sub(r'if \(tvMyListStatus != null\)', r'if (false)', content)

# Fix AniList fields
content = re.sub(r'TextView tvAniListFormat = findViewById\(R\.id\.tvAniListFormat\);', r'// TextView tvAniListFormat', content)
content = re.sub(r'if \(tvAniListFormat != null', r'if (false', content)

content = re.sub(r'TextView tvAniListAuthor = findViewById\(R\.id\.tvAniListAuthor\);', r'// TextView tvAniListAuthor', content)
content = re.sub(r'if \(tvAniListAuthor != null', r'if (false', content)

content = re.sub(r'TextView tvAniListArtist = findViewById\(R\.id\.tvAniListArtist\);', r'// TextView tvAniListArtist', content)
content = re.sub(r'if \(tvAniListArtist != null', r'if (false', content)

content = re.sub(r'TextView tvAniListDates = findViewById\(R\.id\.tvAniListDates\);', r'// TextView tvAniListDates', content)
content = re.sub(r'if \(tvAniListDates != null', r'if (false', content)

with open('app/src/main/java/com/fire/mangareader/activity/MangaDetailActivity.java', 'w') as f:
    f.write(content)

