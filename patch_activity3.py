with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "r") as f:
    lines = f.readlines()

new_lines = []
skip = False
for line in lines:
    if 'composable("settings") { SettingsScreen() },' in line:
        new_lines.append('                        composable("settings") { SettingsScreen() }\n')
        skip = True
        continue
    if skip:
        if '}' in line and ')' in line and 'onNotificationsClick' not in line:
            skip = False
        continue
    new_lines.append(line)

with open("app/src/main/java/com/fire/mangareader/presentation/activity/MainComposeActivity.kt", "w") as f:
    f.writelines(new_lines)
