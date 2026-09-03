import re

filepath = 'app/src/main/java/com/fire/mangareader/presentation/activity/SplashActivity.java'
with open(filepath, 'r') as f:
    content = f.read()

content = content.replace('intent = new Intent(this, MainActivity.class);', 
                          'intent = new Intent(this, MainComposeActivity.class);')

with open(filepath, 'w') as f:
    f.write(content)
print("Patched SplashActivity.java")
