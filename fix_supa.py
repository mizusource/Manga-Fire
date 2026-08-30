with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'r') as f:
    content = f.read()

# The file might have `// ==== NOTIFICATIONS ====` at the end after `}`
import re
new_content = re.sub(r'\}\s*\}\s*// ==== NOTIFICATIONS ====.*', '} }', content, flags=re.DOTALL)

with open('app/src/main/java/com/fire/mangareader/network/SupabaseManager.java', 'w') as f:
    f.write(new_content)
