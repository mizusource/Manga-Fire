with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "r") as f:
    lines = f.readlines()

new_lines = []
skip = False
for line in lines:
    if "private void setupRecentReading()" in line:
        skip = True
    if skip and "protected void onResume()" in line:
        pass # we keep skipping
    if skip and "private void loadHomePageViaWebView(" in line:
        skip = False
    
    if not skip:
        new_lines.append(line)

with open("app/src/main/java/com/fire/mangareader/activity/MainActivity.java", "w") as f:
    f.writelines(new_lines)
