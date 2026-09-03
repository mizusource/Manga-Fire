with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content = f.read()

import re
content = re.sub(r"@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*fun ProfileScreen\(\s*onDownloadsClick: \(\) -> Unit = \{\},\s*viewModel: ProfileViewModel = viewModel\(\)\s*\) \{", """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onDownloadsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {""", content)


row_replacement = """
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onNotificationsClick() }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("الإشعارات")
                                Text("تنبيهات الفصول الجديدة", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(androidx.compose.material.icons.Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
"""

if "الإشعارات" not in content:
    content = content.replace("HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))", "HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))\n" + row_replacement, 1)

if "import androidx.compose.material.icons.filled.Notifications" not in content:
    content = content.replace("import androidx.compose.material.icons.filled.PlayArrow", "import androidx.compose.material.icons.filled.PlayArrow\nimport androidx.compose.material.icons.filled.Notifications")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.write(content)

