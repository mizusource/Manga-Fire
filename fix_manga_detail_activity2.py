import re

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    content = f.read()

bad_block = """    }
    }
    
    private void toggleFavorite() {"""

good_block = """    }
    
    private void toggleFavorite() {"""

content = content.replace(bad_block, good_block)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    f.write(content)
