package com.example.task_1.ui.category

import android.R.attr.onClick
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.task_1.domain.Category
import com.example.task_1.domain.CategoryViewModel
import com.example.task_1.domain.UiState
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.theme.border

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

            is UiState.Success<*> -> LazyColumn(modifier= Modifier.fillMaxSize()) {
                item {
                    Row() {
                        Text(
                            "Categories",
                            modifier = modifier,
                            style = style,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Button(onClick = addCategoryOnClick) {
                            Text("+", style = MaterialTheme.typography.bodyLarge)
                        }
                    }

                    Column() {
                        categories.forEachIndexed { index, category ->
                            Row() {
                                Text(category.text + " " + category.icon)
                                Button(onClick = {
                                    viewModel.indexForEdit = index; editCategoryOnClick()
                                }) { Text("edit") }
                                Button(onClick = {
                                    viewModel.indexForDeletion = index; categoryDeleteDialog()
                                })
                                { Text("X") }
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
                Column() {
                    Text("Are you sure that you want to delete ${categories[viewModel.indexForDeletion].text}")
                    Row() {
                        Button(onClick = { viewModel.removeCategory(viewModel.indexForDeletion); returnToCategoryScreen() }) {
                            Text("OK")
                        }
                        Button(onClick = { returnToCategoryScreen() }) {
                            Text("Cancel")
                        }
                    }}
            }//@formatter:on
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
                        categoryText = it
                    },
                    label = { Text("Text") }
                )
                OutlinedTextField(
                    value = categoryIcon,
                    onValueChange = {
                        categoryIcon = it
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
                        categoryText = it
                    },
                    label = { Text("Text") }
                )
                OutlinedTextField(
                    value = categoryIcon,
                    onValueChange = {
                        categoryIcon = it
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