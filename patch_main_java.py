with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    content = f.read()

# Remove references to vpHeroBanner
import re
content = re.sub(r'ViewPager2 vpHeroBanner.*?;\n', '', content)
content = re.sub(r'vpHeroBanner.*?;\n', '', content)
content = re.sub(r'setupHeroBanner.*?;\n', '', content)

# Remove the method setupHeroBanner if it exists
# I'll just comment out the whole method
import re
pattern = r'private void setupHeroBanner.*?\{.*?\n    \}'
content = re.sub(pattern, '', content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.write(content)
