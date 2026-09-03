with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    lines = f.readlines()

# find where "if (showQualityDialog) {" starts
start_idx = -1
for i, line in enumerate(lines):
    if "if (showQualityDialog) {" in line:
        start_idx = i
        break

# find the closing bracket of showQualityDialog
# it's usually `        )` followed by `    }`
end_idx = -1
if start_idx != -1:
    for i in range(start_idx, len(lines)):
        if "        )" in lines[i] and "    }" in lines[i+1]:
            end_idx = i + 1
            break

if start_idx != -1 and end_idx != -1:
    correct_dialog = """    if (showQualityDialog) {
        val options = listOf("منخفضة", "متوسطة", "عالية")
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("جودة الصور") },
            text = {
                Column {
                    options.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (text == selectedQuality),
                                    onClick = {
                                        selectedQuality = text
                                        sharedPreferences.edit().putString("image_quality", text).apply()
                                        showQualityDialog = false
                                    }
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedQuality),
                                onClick = {
                                    selectedQuality = text
                                    sharedPreferences.edit().putString("image_quality", text).apply()
                                    showQualityDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = text)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
"""
    lines[start_idx:end_idx+1] = [correct_dialog]

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.writelines(lines)
