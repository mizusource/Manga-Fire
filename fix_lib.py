with open("app/src/main/java/com/fire/mangareader/presentation/activity/LibraryActivity.java", "r") as f:
    content = f.read()

# find where the correct class ends
correct_end = content.find("    }\n}\n                        }).start();")
if correct_end != -1:
    content = content[:correct_end + 6]

with open("app/src/main/java/com/fire/mangareader/presentation/activity/LibraryActivity.java", "w") as f:
    f.write(content)

