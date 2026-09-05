package com.fire.mangareader.presentation.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun RatingDialog(
    onDismissRequest: () -> Unit,
    onRatingSubmit: (Float, Float, Float) -> Unit
) {
    var storyRating by remember { mutableFloatStateOf(5f) }
    var charRating by remember { mutableFloatStateOf(5f) }
    val artRating by remember { mutableFloatStateOf(5f) } // Will be mutable
    var artRatingState by remember { mutableFloatStateOf(5f) }

    val averageRating = (storyRating + charRating + artRatingState) / 3f

    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("تقييم العمل", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                RatingSlider(label = "القصة", value = storyRating, onValueChange = { storyRating = it })
                Spacer(modifier = Modifier.height(16.dp))
                
                RatingSlider(label = "الشخصيات", value = charRating, onValueChange = { charRating = it })
                Spacer(modifier = Modifier.height(16.dp))
                
                RatingSlider(label = "الرسم", value = artRatingState, onValueChange = { artRatingState = it })
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.1f", averageRating),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("/ 10", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismissRequest) {
                        Text("إلغاء")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { 
                        onRatingSubmit(storyRating, charRating, artRatingState)
                        onDismissRequest() 
                    }) {
                        Text("حفظ التقييم")
                    }
                }
            }
        }
    }
}

@Composable
fun RatingSlider(label: String, value: Float, onValueChange: (Float) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontWeight = FontWeight.Bold)
            Text(String.format("%.1f", value))
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..10f,
            steps = 9
        )
    }
}
