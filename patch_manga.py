import re

with open("app/src/main/java/com/fire/mangareader/domain/model/Manga.java", "r") as f:
    content = f.read()

new_fields = """
    private boolean isFavorite;
    
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
"""

if "isFavorite" not in content:
    content = content.replace("public Manga() {}", new_fields.strip() + "\n\n    public Manga() {}")

with open("app/src/main/java/com/fire/mangareader/domain/model/Manga.java", "w") as f:
    f.write(content)

