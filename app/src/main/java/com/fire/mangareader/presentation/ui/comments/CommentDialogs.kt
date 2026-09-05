package com.fire.mangareader.presentation.ui.comments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SortCommentsDialog(
    onDismissRequest: () -> Unit,
    onSortSelected: (String, Boolean) -> Unit
) {
    val sortOptions = listOf("الأحدث", "الأقدم", "أفضل التعليقات")
    var selectedOption by remember { mutableStateOf(sortOptions[0]) }
    var hideIrrelevant by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("ترتيب التعليقات", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                sortOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { selectedOption = text },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = hideIrrelevant,
                        onCheckedChange = { hideIrrelevant = it }
                    )
                    Text("إخفاء التعليقات غير ذات الصلة", modifier = Modifier.padding(start = 8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                onSortSelected(selectedOption, hideIrrelevant)
                onDismissRequest() 
            }) {
                Text("موافق")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun ReportCommentDialog(
    onDismissRequest: () -> Unit,
    onReportSubmitted: (String) -> Unit
) {
    val reportOptions = listOf("حرق الاحداث", "تعليق مسيء", "تعليق خارج الموضوع")
    var selectedOption by remember { mutableStateOf(reportOptions[0]) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("الإبلاغ عن تعليق", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                reportOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { selectedOption = text },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { 
                onReportSubmitted(selectedOption)
                onDismissRequest() 
            }) {
                Text("إرسال الإبلاغ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("إلغاء")
            }
        }
    )
}
