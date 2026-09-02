import re

# 1. Update AniListMetadata.java
metadata_path = 'app/src/main/java/com/fire/mangareader/domain/model/AniListMetadata.java'
with open(metadata_path, 'r') as f:
    meta_content = f.read()

if 'public java.util.List<String> genres' not in meta_content:
    meta_content = meta_content.replace('public int popularity = 0;', 'public int popularity = 0;\n    public java.util.List<String> genres = new java.util.ArrayList<>();')
    with open(metadata_path, 'w') as f:
        f.write(meta_content)

# 2. Update AniListManager.java
manager_path = 'app/src/main/java/com/fire/mangareader/util/AniListManager.java'
with open(manager_path, 'r') as f:
    man_content = f.read()

# Add genres to query
man_content = man_content.replace('"averageScore " +', '"averageScore " +\n                        "genres " +')

# Parse genres
parse_logic = """
                        meta.popularity = media.optInt("popularity", 1250);
                        
                        org.json.JSONArray genresArray = media.optJSONArray("genres");
                        if (genresArray != null) {
                            for (int i = 0; i < genresArray.length(); i++) {
                                meta.genres.add(genresArray.getString(i));
                            }
                        }
"""
man_content = man_content.replace('meta.popularity = media.optInt("popularity", 1250);', parse_logic)

with open(manager_path, 'w') as f:
    f.write(man_content)
    
print("Patched AniListManager and Metadata for genres")
