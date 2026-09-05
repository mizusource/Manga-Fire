package com.fire.mangareader.presentation.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {}
) {
    var isDarkMode by remember { mutableStateOf(false) }
    var isNotificationsEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الإعدادات") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // General Settings
            SettingsHeader(title = "عام")
            
            SettingsSwitchItem(
                title = "تفعيل الوضع الليلي",
                isChecked = isDarkMode,
                onCheckedChange = { isDarkMode = it }
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            
            SettingsItem(
                title = "إعدادات القارئ",
                onClick = { /* TODO */ }
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            
            SettingsActionItem(
                title = "مسح الذاكرة المؤقتة",
                actionText = "12mb",
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsItem(
                title = "تغيير جودة الصور",
                onClick = { /* TODO */ }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsSwitchItem(
                title = "تفعيل الإشعارات",
                isChecked = isNotificationsEnabled,
                onCheckedChange = { isNotificationsEnabled = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Other Settings
            SettingsHeader(title = "أخرى")
            
            SettingsItem(
                title = "مشاركة التطبيق",
                onClick = { /* TODO */ }
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            
            SettingsItem(
                title = "حول التطبيق",
                onClick = { /* TODO */ }
            )
            
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        }
    }
}

@Composable
fun SettingsHeader(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SettingsActionItem(title: String, actionText: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = actionText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SettingsSwitchItem(title: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}
