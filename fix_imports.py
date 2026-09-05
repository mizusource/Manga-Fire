import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "r") as f:
    content = f.read()

# Find all import lines
lines = content.split('\n')
non_imports = []
imports = set()
for line in lines:
    if line.startswith('import '):
        if 'androidx.compose.material.icons' in line:
            # We'll re-add these properly
            continue
        imports.add(line)
    else:
        non_imports.append(line)

# Add standard icon imports
imports.add('import androidx.compose.material.icons.Icons')
imports.add('import androidx.compose.material.icons.filled.*')
imports.add('import androidx.compose.material.icons.outlined.*')
imports.add('import androidx.compose.material.icons.automirrored.filled.*')

# Reconstruct file
header = ""
body = ""
is_header = True
for line in non_imports:
    if line.startswith('package '):
        header += line + '\n\n'
    elif line.startswith('@') or line.startswith('fun ') or line.startswith('class '):
        is_header = False
        body += line + '\n'
    elif is_header and line.strip() == '':
        pass
    else:
        body += line + '\n'

final_content = header + '\n'.join(sorted(list(imports))) + '\n\n' + body

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/detail/MangaDetailScreen.kt", "w") as f:
    f.write(final_content)

