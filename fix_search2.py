with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchScreen.kt", "r") as f:
    content = f.read()

# Remove the incorrectly prepended lines
lines = content.split('\n')
if lines[0].startswith("import ") and "package" not in lines[0]:
    # find where package starts
    pkg_idx = 0
    for i, line in enumerate(lines):
        if line.startswith("package "):
            pkg_idx = i
            break
    
    # move the package declaration to the top
    pkg_line = lines[pkg_idx]
    
    # recreate lines
    new_lines = [pkg_line] + lines[:pkg_idx] + lines[pkg_idx+1:]
    content = '\n'.join(new_lines)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/search/SearchScreen.kt", "w") as f:
    f.write(content)
