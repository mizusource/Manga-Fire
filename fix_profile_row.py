import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content = f.read()

# I will find the row I added, remove it, and put it in the correct place.
row_code = """
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showParserSyncDialog = true }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("تحديث إعدادات المحرك")
                                Text("تخطي الحجب والمصادر", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }"""
content = content.replace(row_code, "")

# The correct place is right before showClearCacheDialog in the LazyColumn.
# Let's search for the "تفريغ المساحة (Clear Cache)" row.

target = """                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showClearCacheDialog = true }.padding(vertical = 8.dp),"""
                            
correct_row_code = """                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showParserSyncDialog = true }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("تحديث إعدادات المحرك")
                                Text("تخطي الحجب والمصادر", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(androidx.compose.material.icons.Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
"""

content = content.replace(target, correct_row_code + target)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.write(content)
