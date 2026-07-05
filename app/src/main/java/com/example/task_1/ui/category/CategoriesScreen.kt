package com.example.task_1.ui.category

import android.app.Dialog
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.example.task_1.R
import com.example.task_1.domain.Category
import com.example.task_1.domain.CategoryUiState
import com.example.task_1.domain.MAX_CATEGORY_LENGTH
import com.example.task_1.domain.TransactionUiState
import com.example.task_1.ui.ErrorDialog
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing

@Composable
fun CategoriesScreen(

    viewModel: CategoryViewModel,
    addCategoryOnClick: () -> Unit,
    editCategoryOnClick: (Int) -> Unit,
    categoryDeleteDialog: (Int) -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = uiState is CategoryUiState.Loading
    val categories = (uiState as? CategoryUiState.Success)?.categories ?: mapOf()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData() }
    ) {
        when (uiState) {
            is CategoryUiState.Loading ->
                LoadingScreen()

            is CategoryUiState.Error ->
                ErrorDialog(
                    message = (uiState as CategoryUiState.Error).message,
                    args=(uiState as CategoryUiState.Error).args,
                    loadData = { viewModel.loadData() } )

            is CategoryUiState.Success -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.medium)
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            stringResource(R.string.categories),
                             style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(
                            onClick = addCategoryOnClick,
                            Modifier.clip(MaterialTheme.shapes.small),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("+", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Spacer(Modifier.padding(MaterialTheme.spacing.medium))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            categories.forEach { (categoryID, category) ->
                                Box(
                                    modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "${category.text} ${category.icon}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.padding(MaterialTheme.spacing.medium))
                                }

                            }
                        }
                        Column {
                            categories.forEach { (categoryID, _) ->
                                Button(
                                    onClick = {
                                        //    viewModel.categoryIDForEdit = categoryID
                                        editCategoryOnClick(categoryID)
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(categories[categoryID]?.color ?: 0)
                                    )                                 ) {
                                    Text(stringResource(R.string.edit))
                                }
                                Spacer(Modifier.padding(MaterialTheme.spacing.small))
                            }
                        }

                        Column {
                            categories.forEach { (categoryID, category) ->
                                Button(onClick = {
                                    if ( viewModel.validateIDForDeletion(categoryID) )
                                    categoryDeleteDialog(categoryID)
                                },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(categories[categoryID]?.color ?: 0)
                                    )   ) {
                                    Text("X")
                                }
                                Spacer(Modifier.padding(MaterialTheme.spacing.small))
                            }
                        }
                    }
                }
            }
        }
    }
}
 @Composable
fun CategoryDeleteDialog(categoryIDForDeletion: Int,currentCategory: Category ,returnToCategoryScreen: () -> Unit, removeCategory: (Int) -> Unit) {

    AlertDialog(
        onDismissRequest = returnToCategoryScreen,
        title = { Text(stringResource(R.string.delete_category)) },
        text = { Text(
            stringResource(
                R.string.are_you_sure_that_you_want_to_delete,
                currentCategory.text
            )) },
        confirmButton = {
            TextButton(
                onClick = {
                    removeCategory( categoryIDForDeletion)
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


@Composable
fun AddCategory(returnToCategoryScreen: () -> Unit, viewModel: CategoryViewModel) {
    var categoryText by remember { mutableStateOf("") }
    var categoryIcon by remember { mutableStateOf("") }
    val colorOptions = listOf(
        R.string.blue to Color.Blue.toArgb(),
        R.string.green to Color.Green.toArgb(),
        R.string.cyan to Color.Cyan.toArgb(),
        R.string.magenta to Color.Magenta.toArgb(),
        R.string.teal to Color(0xFF008080).toArgb(),
        R.string.indigo to Color(0xFF4B0082).toArgb(),
        R.string.slate_gray to Color(0xFF708090).toArgb(),
        R.string.sky_blue to Color(0xFF87CEEB).toArgb(),
     )

    var colorExpanded by remember { mutableStateOf(false) }

    var categoryColor by remember { mutableStateOf(colorOptions[0].first to colorOptions[0].second) }

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

        Text(stringResource(R.string.new_category), style = MaterialTheme.typography.titleMedium)
        Column {
            Row() {
                OutlinedTextField(
                    value = categoryText,
                    onValueChange = {
                        if (it.length < MAX_CATEGORY_LENGTH) categoryText = it
                    },
                    label = { Text(stringResource(R.string.text)) }
                )
                OutlinedTextField(
                    value = categoryIcon,
                    onValueChange = {
                        if (it.length < MAX_CATEGORY_LENGTH) categoryIcon = it
                    },
                    label = { Text(stringResource(R.string.icon)) }
                )
            }
            Box {
                Text(
                    text = stringResource(R.string.color, stringResource(categoryColor.first)),
                    color = Color(categoryColor.second),
                    modifier = Modifier
                        .clickable { colorExpanded = true }
                        .padding(MaterialTheme.spacing.medium)
                )
                DropdownMenu(
                    expanded = colorExpanded,
                    onDismissRequest = { colorExpanded = false }) {
                    colorOptions.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(stringResource(item.first), color = Color(item.second)) },
                            onClick = { categoryColor = item; colorExpanded = false }
                        )
                    }
                }
            }


        }
        Button(onClick = {
            viewModel.addCategory(
                Category(
                    categoryText,
                    categoryIcon,
                    categoryColor.second,
                    0.0
                )
            ); returnToCategoryScreen()
        }) {
            Text(stringResource(R.string.add_this_category), style = MaterialTheme.typography.bodyLarge)
        }
        Button(onClick = {
            returnToCategoryScreen()
        }) {
            Text(stringResource(R.string.cancel), style = MaterialTheme.typography.bodyLarge)
        }
    }
}