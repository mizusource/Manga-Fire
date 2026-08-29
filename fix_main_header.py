import re

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'r') as f:
    lines = f.readlines()

new_lines = []
skip = False
for line in lines:
    if line.strip().startswith('private void updateNavHeader()');
        skip = True
        new_lines.append('    private void updateNavHeader() {\n    }\n}\n')
        break
    else:
        new_lines.append(line)

with open('app/src/main/java/com/fire/mangareader/activity/MainActivity.java', 'w') as f:
    f.writelines(new_lines)

