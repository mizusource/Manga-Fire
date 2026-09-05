with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    content = f.read()

# remove everything after the last closing brace of BottomNavigationBar
idx = content.find("fun BottomNavigationBar")
if idx != -1:
    end_idx = content.find("}", content.find("}", content.find("}", idx) + 1) + 1) + 1
    # keep reading until balanced
    brace_count = 0
    in_function = False
    for i in range(idx, len(content)):
        if content[i] == '{':
            brace_count += 1
            in_function = True
        elif content[i] == '}':
            brace_count -= 1
        if in_function and brace_count == 0:
            content = content[:i+1] + "\n"
            break

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.write(content)
