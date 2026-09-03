package com.fire.mangareader.presentation.ui.screens.profile

import androidx.compose.material.icons.filled.Refresh
import android.widget.Toast
import kotlinx.coroutines.launch
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onDownloadsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
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
    var showParserSyncDialog by remember { mutableStateOf(false) }
    var syncUrlText by remember { mutableStateOf(com.fire.mangareader.data.parser.ParserConfigManager.getSyncUrl(context)) }
    val coroutineScope = androidx.compose.runtime.rememberCoroutineScope()

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

if (showParserSyncDialog) {
        AlertDialog(
            onDismissRequest = { showParserSyncDialog = false },
            title = { Text("إعدادات المحرك الديناميكي") },
            text = {
                Column {
                    Text("أدخل رابط ملف הـ JSON الخاص بإعدادات المواقع لتخطي الحجب وتحديث المسارات:")
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = syncUrlText ?: "",
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
                        coroutineScope.launch {
                            val result = com.fire.mangareader.data.parser.ParserConfigManager.syncConfig(context, syncUrlText.trim())
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
        LazyColumn(
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
                            Icon(androidx.compose.material.icons.Icons.Default.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        }
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

@Composable
fun StatCard(title: String, count: Int) {
    Card(
        modifier = Modifier.width(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = title, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
