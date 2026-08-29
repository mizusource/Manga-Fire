with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

# find last occurrence of }
last_brace = content.rfind('}')
if last_brace != -1:
    content = content[:last_brace] + content[last_brace+1:]

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
