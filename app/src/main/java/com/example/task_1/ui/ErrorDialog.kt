package com.example.task_1.ui

import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.task_1.domain.TransactionUiState
import com.example.task_1.ui.theme.ErrorColor

@Composable
fun ErrorDialog( message: String, loadData: ()-> Unit ) {
    AlertDialog(
        modifier = Modifier.background(color = ErrorColor),
        onDismissRequest = { loadData() },
        confirmButton = {
            TextButton(onClick = { loadData() }) {
                Text("OK", color = ErrorColor)
            }
        },
        title = { Text("Error", color = ErrorColor) },
        text = { Text(message) }
    )
}