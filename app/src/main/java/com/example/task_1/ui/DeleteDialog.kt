package com.example.task_1.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.task_1.R


@Composable
fun DeleteDialog(
    iDForDeletion: Long?,
    closeDialog: () -> Unit,
    removeObject: (Long?) -> Unit
) {

    AlertDialog(
        onDismissRequest = closeDialog,
        title = { Text(stringResource(R.string.delete)) },
        text = {
            Text(
                stringResource(R.string.are_you_sure_you_want_to_delete_this)
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    removeObject(iDForDeletion)
                    closeDialog()
                }) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = closeDialog) {
                Text(stringResource(R.string.cancel))
            }
        })
}
