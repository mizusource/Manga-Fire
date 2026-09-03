with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content = f.read()

replacement = """
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onDownloadsClick: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
"""

import re
content = re.sub(r"@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*fun ProfileScreen\(\s*viewModel: ProfileViewModel = viewModel\(\)\s*\) \{", replacement.strip() + " {", content, flags=re.DOTALL)

row_replacement = """
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onDownloadsClick() }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("قائمة التنزيلات")
                                Text("إدارة الفصول المحملة", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
"""

content = content.replace("HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))", row_replacement, 1) # Replace the first divider with this

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.write(content)
