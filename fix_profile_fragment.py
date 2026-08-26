with open("app/src/main/java/com/fire/mangareader/fragment/ProfileFragment.java", "r") as f:
    content = f.read()
content = content.replace("tvBio = view.findViewById(R.id.tvBio);", "")
content = content.replace("tvRank = view.findViewById(R.id.tvRank);", "")
with open("app/src/main/java/com/fire/mangareader/fragment/ProfileFragment.java", "w") as f:
    f.write(content)
