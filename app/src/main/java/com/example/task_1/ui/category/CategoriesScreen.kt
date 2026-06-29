package com.example.task_1.ui.category

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
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
                          ,
                            shape = MaterialTheme.shapes.small) {
                            Text("+", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    Spacer(Modifier.padding(MaterialTheme.spacing.medium))

                    Row (horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                        modifier = Modifier.fillMaxWidth()){
                        Column {
                            categories.forEachIndexed { index, category ->
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
                            categories.forEachIndexed { index, _ ->
                                Button(
                                    onClick = {
                                        viewModel.indexForEdit = index
                                        editCategoryOnClick()
                                    }
                                ) {
                                    Text("Edit")
                                }
                                Spacer(Modifier.padding(MaterialTheme.spacing.small))
                            }
                        }

                        Column {
                            categories.forEachIndexed { index, category ->
                                Button(onClick = {
                                    viewModel.indexForDeletion = index; categoryDeleteDialog()
                                })
                                {
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
                if (viewModel.transactionsInCategory()) {
                    Text("Category ${categories[viewModel.indexForDeletion].text} ${categories[viewModel.indexForDeletion].icon} is active. You cannot delete it.")
                    Button(onClick = { returnToCategoryScreen() }) {
                        Text("Return")
                    }
                } else {
                    Text("Are you sure that you want to delete ${categories[viewModel.indexForDeletion].text}")
                    Row {
                        Button(onClick = { viewModel.removeCategory(viewModel.indexForDeletion); returnToCategoryScreen() }) {
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
    var categoryText by remember { mutableStateOf(categories[viewModel.indexForEdit].text) }
    var categoryIcon by remember { mutableStateOf(categories[viewModel.indexForEdit].icon) }

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
        Row() {
            OutlinedTextField(
                value = categoryText,
                onValueChange = {
                     if ( categoryText.length < MAX_CATEGORY_LENGTH) categoryText = it
                },
                label = { Text("Text") }
            )
            OutlinedTextField(
                value = categoryIcon,
                onValueChange = {
                    if ( categoryIcon.length < MAX_CATEGORY_LENGTH) categoryIcon= it
                },
                label = { Text("Icon") }
            )
        }
        Button(onClick = {
            viewModel.editCategory(
                viewModel.indexForEdit,
                Category(categoryText, categoryIcon)
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
        Row() {
            OutlinedTextField(
                value = categoryText,
                onValueChange = {
                    if (  it.length < MAX_CATEGORY_LENGTH)  categoryText = it
                },
                label = { Text("Text") }
            )
            OutlinedTextField(
                value = categoryIcon,
                onValueChange = {
                    if (  it.length < MAX_CATEGORY_LENGTH) categoryIcon = it
                },
                label = { Text("Icon") }
            )
        }
        Button(onClick = {
            viewModel.addCategory(
                categoryText,
                categoryIcon
            ); returnToCategoryScreen()
        }) {
            Text("Add this category", style = MaterialTheme.typography.bodyLarge)
        }

    }

}