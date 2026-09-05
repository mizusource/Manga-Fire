package com.fire.mangareader.presentation.ui.screens.profile

import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onDownloadsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    var userName by remember { mutableStateOf("اسم المستخدم") }
    var userEmail by remember { mutableStateOf("user@example.com") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("الملف الشخصي") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { /* Sign Out */ }) {
                        Icon(Icons.Default.Logout, contentDescription = "تسجيل خروج", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Avatar Container
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    tint = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = { /* Change Avatar */ },
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "تغيير الصورة", tint = MaterialTheme.colorScheme.secondary)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // User Info Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("الاسم", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(userName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = { /* Edit Name */ }) {
                            Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        Text("البريد الإلكتروني", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(userEmail, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Status Chart (PieChart)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("حالة القراءة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        factory = { context ->
                            PieChart(context).apply {
                                description.isEnabled = false
                                legend.isEnabled = false
                                isDrawHoleEnabled = true
                                setHoleColor(AndroidColor.TRANSPARENT)
                                
                                val entries = listOf(
                                    PieEntry(40f, "مكتمل"),
                                    PieEntry(30f, "مستمر"),
                                    PieEntry(20f, "مخطط له"),
                                    PieEntry(10f, "متوقف")
                                )
                                val dataSet = PieDataSet(entries, "الحالة").apply {
                                    colors = ColorTemplate.MATERIAL_COLORS.toList()
                                    valueTextSize = 14f
                                    valueTextColor = AndroidColor.WHITE
                                }
                                data = PieData(dataSet)
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ratings Chart (BarChart)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("تصويتات التقييمات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        factory = { context ->
                            BarChart(context).apply {
                                description.isEnabled = false
                                legend.isEnabled = false
                                setDrawGridBackground(false)
                                
                                val entries = (1..10).map { i ->
                                    BarEntry(i.toFloat(), (1..20).random().toFloat())
                                }
                                val dataSet = BarDataSet(entries, "التقييمات").apply {
                                    color = AndroidColor.parseColor("#FF5722") // Example color
                                    valueTextSize = 10f
                                    valueTextColor = AndroidColor.WHITE
                                }
                                data = BarData(dataSet)
                                xAxis.position = XAxis.XAxisPosition.BOTTOM
                                xAxis.textColor = AndroidColor.WHITE
                                axisLeft.textColor = AndroidColor.WHITE
                                axisRight.isEnabled = false
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Radar Chart (Stats)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("متوسط تقييم القصة، الشخصيات، والرسم", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    AndroidView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        factory = { context ->
                            RadarChart(context).apply {
                                description.isEnabled = false
                                legend.isEnabled = false
                                webColor = AndroidColor.LTGRAY
                                webColorInner = AndroidColor.LTGRAY
                                webAlpha = 100
                                
                                val entries = listOf(
                                    RadarEntry(8.5f), // Story
                                    RadarEntry(7.0f), // Characters
                                    RadarEntry(9.2f)  // Art
                                )
                                val dataSet = RadarDataSet(entries, "الإحصائيات").apply {
                                    color = AndroidColor.CYAN
                                    fillColor = AndroidColor.CYAN
                                    setDrawFilled(true)
                                    fillAlpha = 180
                                    lineWidth = 2f
                                    isDrawHighlightCircleEnabled = true
                                    setDrawHighlightIndicators(false)
                                    valueTextSize = 12f
                                    valueTextColor = AndroidColor.WHITE
                                }
                                data = RadarData(dataSet)
                            }
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
