package com.example.task_1.ui.category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.example.task_1.R
import com.example.task_1.domain.Category
import com.example.task_1.domain.MAX_CATEGORY_LENGTH
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing

@Composable
fun EditCategory(
    categoryIDForEdit: Int,
    currentCategory: Category,
    returnToCategoryScreen: () -> Unit,
    editCategory: (Int, Category) -> Unit
) {

    var categoryText by remember { mutableStateOf(currentCategory.text) }
    var categoryIcon by remember { mutableStateOf(currentCategory.icon) }
     val colorOptions = listOf(
        R.string.blue to Color.Blue.toArgb(),
        R.string.green to Color.Green.toArgb(),
        R.string.cyan to Color.Cyan.toArgb(),
        R.string.magenta to Color.Magenta.toArgb(),
        R.string.teal to Color(0xFF008080).toArgb(),
        R.string.indigo to Color(0xFF4B0082).toArgb(),
        R.string.slate_gray to Color(0xFF708090).toArgb(),
        R.string.sky_blue to Color(0xFF87CEEB).toArgb(),
        R.string.current_color to currentCategory.color
    )

    var colorExpanded by remember { mutableStateOf(false) }

     var categoryColor by remember { mutableStateOf(R.string.current_color to currentCategory.color) }


    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .border(
                BorderStroke(
                    width = MaterialTheme.border.medium, color = MaterialTheme.colorScheme.primary
                )
            )
    ) {
        Text(stringResource(R.string.edit_category), style = MaterialTheme.typography.titleMedium)
        Column {
            Row {
                OutlinedTextField(value = categoryText, onValueChange = {
                    if (it.length < MAX_CATEGORY_LENGTH) categoryText = it
                }, label = { Text(stringResource(R.string.text)) })
                OutlinedTextField(value = categoryIcon, onValueChange = {
                    if (it.length < MAX_CATEGORY_LENGTH) categoryIcon = it
                }, label = { Text(stringResource(R.string.icon)) })
            }
            Box {
                Text(
                    text = stringResource(R.string.color, stringResource(categoryColor.first)),
                    color = Color(categoryColor.second),
                    modifier = Modifier
                        .clickable { colorExpanded = true }
                        .padding(MaterialTheme.spacing.medium))
                DropdownMenu(
                    expanded = colorExpanded, onDismissRequest = { colorExpanded = false }) {
                    colorOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(stringResource(item.first), color = Color(item.second)) },
                            onClick = { categoryColor = item; colorExpanded = false })
                    }
                }
            }
        }

        Button(onClick = {
            editCategory(
                categoryIDForEdit, Category(
                    categoryText,
                    categoryIcon,
                    categoryColor.second,
                    currentCategory.expenseOnThisCategory
                )
            ); returnToCategoryScreen()
        }) {
            Text(stringResource(R.string.save), style = MaterialTheme.typography.bodyLarge)
        }
    }
}