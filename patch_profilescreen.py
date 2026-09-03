import re

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content = f.read()

# Add state variables
state_vars = """
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showParserSyncDialog by remember { mutableStateOf(false) }
    var syncUrlText by remember { mutableStateOf(com.fire.mangareader.data.parser.ParserConfigManager.INSTANCE.getSyncUrl(context)) }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()
"""
content = content.replace("    var showClearCacheDialog by remember { mutableStateOf(false) }", state_vars.strip())

# Add dialog
dialog_code = """
    if (showParserSyncDialog) {
        AlertDialog(
            onDismissRequest = { showParserSyncDialog = false },
            title = { Text("إعدادات المحرك الديناميكي") },
            text = {
                Column {
                    Text("أدخل رابط ملف הـ JSON الخاص بإعدادات المواقع لتخطي الحجب وتحديث المسارات:")
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = syncUrlText,
                        onValueChange = { syncUrlText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("https://.../config.json") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showParserSyncDialog = false
                        Toast.makeText(context, "جاري التحديث...", Toast.LENGTH_SHORT).show()
                        coroutineScope.kotlinx.coroutines.launch {
                            val result = com.fire.mangareader.data.parser.ParserConfigManager.INSTANCE.syncConfig(context, syncUrlText.trim())
                            if (result.isSuccess) {
                                Toast.makeText(context, "تم التحديث بنجاح!", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "فشل التحديث: خطأ بالاتصال", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                ) {
                    Text("تحديث")
                }
            },
            dismissButton = {
                TextButton(onClick = { showParserSyncDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showClearCacheDialog) {
"""

# Wait, `coroutineScope.kotlinx.coroutines.launch` won't work in Kotlin. It's just `coroutineScope.launch`.
dialog_code = dialog_code.replace("coroutineScope.kotlinx.coroutines.launch", "coroutineScope.launch")

content = content.replace("    if (showClearCacheDialog) {", dialog_code.strip() + "\n\n    if (showClearCacheDialog) {")

# Add row in the list
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
                        }
"""
content = content.replace("                    }", row_code + "\n                    }", 1)

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.write(content)
