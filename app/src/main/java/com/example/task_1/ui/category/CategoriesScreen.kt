package com.example.task_1.ui.category

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.room.util.getColumnIndex
import androidx.room.util.performInTransactionSuspending
import com.example.task_1.domain.Category
import com.example.task_1.ui.category.CategoryViewModel
import com.example.task_1.domain.MAX_CATEGORY_LENGTH
import com.example.task_1.domain.MAX_RECEIVER_LENGTH
import com.example.task_1.domain.UiState
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing

@Composable
fun CategoriesScreen(
    modifier: Modifier,
    style: TextStyle,
    viewModel: CategoryViewModel,
    addCategoryOnClick: () -> Unit,
    editCategoryOnClick: () -> Unit,
    categoryDeleteDialog: () -> Unit
) {

    val categories by viewModel.categories.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = uiState is UiState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData() }
    ) {
        when (uiState) {
            UiState.Loading ->
                LoadingScreen()

            is UiState.Error ->
                Text((uiState as UiState.Error).message)

            is UiState.Success<*> -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = MaterialTheme.spacing.medium)
            ) {
                item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Categories",
                            modifier = modifier,
                            style = style,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(onClick = addCategoryOnClick, Modifier
                            .clip(MaterialTheme.shapes.small)
                            ,shape = MaterialTheme.shapes.small) {
                            Text("+", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Spacer(Modifier.padding(MaterialTheme.spacing.medium))

                    Row (horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                        modifier = Modifier.fillMaxWidth()){
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
                                        viewModel.categoryIDForEdit = categoryID
                                        editCategoryOnClick()
                                    }
                                ) {
                                    Text("Edit")
                                }
                                Spacer(Modifier.padding(MaterialTheme.spacing.small))
                            }
                        }

                        Column {
                            categories.forEach { (categoryID, category) ->
                                Button(onClick = {
                                    viewModel.categoryIDForDeletion = categoryID;
                                    categoryDeleteDialog()
                                }) {
                                    Text("X") }
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
fun CategoryDeleteDialog(returnToCategoryScreen: () -> Unit, viewModel: CategoryViewModel) {
    val categories by viewModel.categories.collectAsState()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier.border(
                width = MaterialTheme.border.medium,
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.large
            ), contentAlignment = Alignment.Center
        ) {
            Column {
                // TODO these ifs should be only in the viewmodel
                if (viewModel.transactionsInCategory(viewModel.categoryIDForDeletion)) {
                    Text("Category ${categories[viewModel.categoryIDForDeletion]?.text} ${categories[viewModel.categoryIDForDeletion]?.icon} is active. You cannot delete it.")
                    Button(onClick = { returnToCategoryScreen() }) {
                        Text("Return")
                    }
                } else {
                    Text("Are you sure that you want to delete ${categories[viewModel.categoryIDForDeletion]?.text}")
                    Row {
                        Button(onClick = { viewModel.removeCategory(viewModel.categoryIDForDeletion); returnToCategoryScreen() }) {
                            Text("OK")
                        }
                        Button(onClick = { returnToCategoryScreen() }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditCategory(returnToCategoryScreen: () -> Unit, viewModel: CategoryViewModel) {


    val categories by viewModel.categories.collectAsState()
    val currentCategory = categories[viewModel.categoryIDForEdit] ?: Category(text = "", icon = "", color=Color.Black)

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
            viewModel.editCategory(
                viewModel.categoryIDForEdit,
                Category(categoryText,
                                        categoryIcon,
                                categoryColor.second)
            ); returnToCategoryScreen()
        }) {
            Text("Save", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun AddCategory(returnToCategoryScreen: () -> Unit, viewModel: CategoryViewModel) {
    var categoryText by remember { mutableStateOf("") }
    var categoryIcon by remember { mutableStateOf("") }
    val colorOptions = listOf("Blue" to Color.Blue, "Green" to Color.Green, "Red" to Color.Red)
    var colorExpanded by remember { mutableStateOf(false) }
    var categoryColor by remember { mutableStateOf(colorOptions[0]) }

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

        Text("New Category", style = MaterialTheme.typography.titleMedium)
        Column {
            Row() {
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
            Box {
                Text(
                    text = "Color: ${categoryColor.first}",
                    color = categoryColor.second,
                    modifier = Modifier.clickable { colorExpanded = true }
                        .padding(MaterialTheme.spacing.medium)
                )
                DropdownMenu(
                    expanded = colorExpanded,
                    onDismissRequest = { colorExpanded = false }) {
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
            viewModel.addCategory(
                Category(
                    categoryText,
                    categoryIcon,
                    categoryColor.second
                )
            ); returnToCategoryScreen()
        }) {
            Text("Add this category", style = MaterialTheme.typography.bodyLarge)
        }
        Button(onClick = {
            returnToCategoryScreen()
        }) {
            Text("Cancel", style = MaterialTheme.typography.bodyLarge)
        }
    }
}