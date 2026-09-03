with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content = f.read()

import re

row_replacement = """
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onNotificationsClick() }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("صندوق الإشعارات")
                                Text("عرض التنبيهات السابقة", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(androidx.compose.material.icons.Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
"""

# Find the downloads row and insert the notifications row after it
downloads_row_pattern = r'Row\(\s*modifier = Modifier\.fillMaxWidth\(\)\.clickable \{ onDownloadsClick\(\) \}\.padding\(vertical = 8\.dp\),.*?Icon\(Icons\.Default\.PlayArrow, contentDescription = null, tint = MaterialTheme\.colorScheme\.primary\)\s*\}'
if "صندوق الإشعارات" not in content:
    match = re.search(downloads_row_pattern, content, flags=re.DOTALL)
    if match:
        content = content[:match.end()] + "\n                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))\n" + row_replacement + content[match.end():]
    else:
        print("Could not find downloads row")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.write(content)
