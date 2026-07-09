package com.example.task_1.ui.category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.example.task_1.R
import com.example.task_1.domain.Category
import com.example.task_1.domain.MAX_CATEGORY_LENGTH
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing

@Composable
fun CategoryForm(
    currentCategory: Category?,
    actionOnClick: (Category) -> Unit,
    onCancel: () -> Unit
) {
    var categoryText by remember { mutableStateOf(currentCategory?.text ?: "") }
    var categoryIcon by remember { mutableStateOf(currentCategory?.icon ?: "") }
    val colorOptions = listOf(
        R.string.blue to Color.Blue.toArgb(),
        R.string.green to Color.Green.toArgb(),
        R.string.cyan to Color.Cyan.toArgb(),
        R.string.magenta to Color.Magenta.toArgb(),
        R.string.teal to Color(0xFF008080).toArgb(),
        R.string.indigo to Color(0xFF4B0082).toArgb(),
        R.string.slate_gray to Color(0xFF708090).toArgb(),
        R.string.sky_blue to Color(0xFF87CEEB).toArgb(),
    ) // TODO get them from database

    var colorExpanded by remember { mutableStateOf(false) }

    var categoryColor by remember {
        mutableStateOf(currentCategory?.let { currentCategory ->
            R.string.current_color to currentCategory.color
        } ?: colorOptions.first())
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
            .clip(MaterialTheme.shapes.medium)
            .border(
                BorderStroke(
                    width = MaterialTheme.border.medium,
                    color = MaterialTheme.colorScheme.primary
                )
            )
            .padding(MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {

        Text(stringResource(R.string.new_category), style = MaterialTheme.typography.titleMedium)
        Column {
            OutlinedTextField(
                value = categoryText,
                onValueChange = {
                    if (it.length < MAX_CATEGORY_LENGTH) categoryText = it
                },
                label = { Text(stringResource(R.string.text)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = categoryIcon,
                onValueChange = {
                    if (it.length < MAX_CATEGORY_LENGTH) categoryIcon = it
                },
                label = { Text(stringResource(R.string.icon)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = stringResource(categoryColor.first),
                    onValueChange = { },
                    label = { Text(stringResource(com.example.task_1.R.string.color)) },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = Color(categoryColor.second)
                    )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { colorExpanded = !colorExpanded }
                )
            }
            DropdownMenu(
                expanded = colorExpanded,
                onDismissRequest = { colorExpanded = false },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                colorOptions.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(stringResource(item.first), color = Color(item.second)) },
                        onClick = { categoryColor = item; colorExpanded = false }
                    )
                }
            }
        }

        Button(onClick = {
            actionOnClick(
                Category(
                    currentCategory?.id,
                    categoryText,
                    categoryIcon,
                    categoryColor.second
                )
            );
        }) {
            Text(
                stringResource(R.string.save),
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Button(onClick = onCancel) {
            Text(stringResource(R.string.cancel), style = MaterialTheme.typography.bodyLarge)
        }
    }

}
