with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "r") as f:
    lines = f.readlines()

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MangaDetailActivity.java", "w") as f:
    for i, line in enumerate(lines):
        if line.strip() == "}" and lines[i-1].strip() == "}":
            if "toggleFavorite" in "".join(lines[i:i+3]):
                print("Skipping line", i)
                continue
        f.write(line)
