import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content = f.read()

content = content.replace("value = syncUrlText,", 'value = syncUrlText ?: "",')

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.write(content)
