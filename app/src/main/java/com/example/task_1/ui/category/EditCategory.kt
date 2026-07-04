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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.task_1.domain.Category
import com.example.task_1.domain.MAX_CATEGORY_LENGTH
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing

@Composable
fun EditCategory(categoryIDForEdit: Int, currentCategory: Category, returnToCategoryScreen: () -> Unit, editCategory: (Int, Category) -> Unit) {

    var categoryText by remember { mutableStateOf(currentCategory.text) }
    var categoryIcon by remember { mutableStateOf(currentCategory.icon)}
    val colorOptions = listOf("Blue" to Color.Blue, "Green" to Color.Green, "Red" to Color.Red) // TODO more colors, not hardcoded
    var colorExpanded by remember { mutableStateOf(false) }
    var categoryColor by remember { mutableStateOf("current" to currentCategory.color) }

    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.large)
            .border(
                BorderStroke(
                    width = MaterialTheme.border.medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
    ) {
        Text("Edit Category", style = MaterialTheme.typography.titleMedium)
        Column {
            Row {
                OutlinedTextField(
                    value = categoryText,
                    onValueChange = {
                        if (it.length < MAX_CATEGORY_LENGTH) categoryText = it
                    },
                    label = { Text("Text") }
                )
                OutlinedTextField(
                    value = categoryIcon,
                    onValueChange = {
                        if (it.length < MAX_CATEGORY_LENGTH) categoryIcon = it
                    },
                    label = { Text("Icon") }
                )
            }
            Box{
                Text(
                    text = "Color: ${categoryColor.first}",
                    color = categoryColor.second,
                    modifier = Modifier.clickable { colorExpanded = true }
                        .padding(MaterialTheme.spacing.medium)
                )
                DropdownMenu(expanded = colorExpanded, onDismissRequest = { colorExpanded = false }) {
                    colorOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(item.first, color = item.second) },
                            onClick = { categoryColor = item; colorExpanded = false }
                        )
                    }
                }
            }
        }

        Button(onClick = {
            editCategory(
                categoryIDForEdit,
                Category(categoryText,
                    categoryIcon,
                    categoryColor.second,
                    currentCategory.expenseOnThisCategory)
            ); returnToCategoryScreen()
        }) {
            Text("Save", style = MaterialTheme.typography.bodyLarge)
        }
    }
}