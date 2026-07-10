package com.example.task_1.ui

import androidx.compose.foundation.background
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.task_1.R
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.uiStates.TransactionUiState
import com.example.task_1.ui.theme.ErrorColor

@Composable
fun ErrorDialog( message: ErrorMessage, loadData: ()-> Unit ) {
    AlertDialog(
        modifier = Modifier.background(color = ErrorColor),
        onDismissRequest = { loadData() },
        confirmButton = {
            TextButton(onClick = { loadData() }) {
                Text(stringResource(R.string.retry), color = ErrorColor)
            }
        },
        title = { Text(stringResource(R.string.error), color = ErrorColor) },
        text = { Text(stringResource(message.messageID, *message.args.toTypedArray())) }
    )
}