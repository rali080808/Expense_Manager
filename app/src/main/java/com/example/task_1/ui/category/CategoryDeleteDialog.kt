package com.example.task_1.ui.category

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.task_1.R
import com.example.task_1.domain.Category


@Composable
fun CategoryDeleteDialog(
    categoryIDForDeletion: Int,
    currentCategory: Category,
    returnToCategoryScreen: () -> Unit,
    removeCategory: (Int) -> Unit
) {

    AlertDialog(
        onDismissRequest = returnToCategoryScreen,
        title = { Text(stringResource(R.string.delete_category)) },
        text = {
            Text(
                stringResource(
                    R.string.are_you_sure_that_you_want_to_delete,
                    currentCategory.text
                )
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    removeCategory(categoryIDForDeletion)
                    returnToCategoryScreen()
                }
            ) {
                Text(stringResource(R.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = returnToCategoryScreen) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
