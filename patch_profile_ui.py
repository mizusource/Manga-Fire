with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "r") as f:
    content = f.read()

replacement = """
import android.content.Context
import androidx.compose.foundation.selection.selectable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsState()
    val profileImageUri by viewModel.profileImageUri.collectAsState()
    val favoritesCount by viewModel.favoritesCount.collectAsState()
    val historyCount by viewModel.historyCount.collectAsState()
    val cacheSize by viewModel.cacheSize.collectAsState()
    
    var showNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }

    val sharedPreferences = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }
    var notificationsEnabled by remember { mutableStateOf(sharedPreferences.getBoolean("notifications_enabled", true)) }
    var isDarkMode by remember { mutableStateOf(sharedPreferences.getBoolean("dark_mode", true)) }
    var selectedQuality by remember { mutableStateOf(sharedPreferences.getString("image_quality", "متوسطة") ?: "متوسطة") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri: Uri? ->
            uri?.let { 
                try {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    context.contentResolver.takePersistableUriPermission(it, flag)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                viewModel.updateProfileImage(it.toString()) 
            }
        }
    )

    if (showNameDialog) {
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("تغيير الاسم") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("الاسم") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateUserName(tempName)
                    showNameDialog = false
                }) {
                    Text("حفظ")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    if (showQualityDialog) {
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

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("حذف البيانات المؤقتة") },
            text = { Text("سيتم مسح جميع الملفات المؤقتة والصور لتفريغ المساحة. هل أنت متأكد؟") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCache()
                        showClearCacheDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("حذف")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الملف الشخصي") }
            )
        }
    ) { padding ->
        // Using a LazyColumn or vertical scroll is better here
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            imagePickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri.isNotEmpty()) {
                        AsyncImage(
                            model = profileImageUri,
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // User Name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = {
                        tempName = userName
                        showNameDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Name")
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                // Stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatCard(title = "المفضلة", count = favoritesCount)
                    StatCard(title = "تمت قراءتها", count = historyCount)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            item {
                // App Settings
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("إعدادات التطبيق", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("الإشعارات")
                            Switch(
                                checked = notificationsEnabled, 
                                onCheckedChange = { 
                                    notificationsEnabled = it
                                    sharedPreferences.edit().putBoolean("notifications_enabled", it).apply()
                                }
                            )
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showQualityDialog = true }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("جودة الصور")
                                Text(selectedQuality, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { showClearCacheDialog = true }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("تفريغ المساحة (Clear Cache)")
                                Text("المساحة الحالية: $cacheSize", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
"""

import re
pattern = re.compile(r"@OptIn\(ExperimentalMaterial3Api::class\)\s*@Composable\s*fun ProfileScreen\(\s*viewModel: ProfileViewModel = viewModel\(\)\s*\) \{.*?\}\s*\}", re.DOTALL)
content = pattern.sub(replacement.strip(), content)

# ensure android.content.Context and foundation.selection.selectable are there
if "import android.content.Context" not in content:
    content = content.replace("import android.content.Intent", "import android.content.Intent\nimport android.content.Context\nimport androidx.compose.foundation.selection.selectable")

with open("app/src/main/java/com/fire/mangareader/presentation/ui/screens/profile/ProfileScreen.kt", "w") as f:
    f.write(content)
