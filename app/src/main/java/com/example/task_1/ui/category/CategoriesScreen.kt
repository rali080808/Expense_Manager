package com.example.task_1.ui.category

import android.app.Dialog
import android.util.MutableBoolean
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.task_1.domain.uiStates.CategoryUiState
import com.example.task_1.domain.ComponentMode
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.domain.MAX_CATEGORY_LENGTH
import com.example.task_1.domain.uiStates.TransactionUiState
import com.example.task_1.ui.ErrorDialog
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing
import com.example.task_1.ui.transaction.TransactionForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(

    viewModel: CategoryViewModel,
    addCategory: (Category) -> Unit,
    editCategory: (Int, Category) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = uiState is CategoryUiState.Loading
    val categories = (uiState as? CategoryUiState.Success)?.categories ?: mapOf()
    var showCategoryForm by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var componentMode by remember { mutableStateOf(ComponentMode.ADD) }
    var clickedCategoryID by remember { mutableIntStateOf(-1) }
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
                    args = (uiState as CategoryUiState.Error).args,
                    loadData = { viewModel.loadData() })

            is CategoryUiState.Success -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = MaterialTheme.spacing.medium)
            ) {
                item {
                    if (showCategoryForm) {
                        ModalBottomSheet(
                            onDismissRequest = { showCategoryForm = false },
                            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                        ) {
                            CategoryForm(
                                currentCategory = if (componentMode == ComponentMode.EDIT) categories[clickedCategoryID] else null,
                                actionOnClick = { category ->
                                    if (componentMode == ComponentMode.ADD)
                                        addCategory(category)
                                    else
                                        editCategory(clickedCategoryID, category)
                                    showCategoryForm = false
                                },
                                onCancel = { showCategoryForm = false }
                            )
                        }
                    }
                    if (showDeleteDialog) {
                        CategoryDeleteDialog(
                            categoryIDForDeletion = clickedCategoryID,
                            currentCategory = categories[clickedCategoryID] ?: ErrorCategory,
                            closeDialog = { showDeleteDialog = false },
                            removeCategory = { id -> viewModel.removeCategory(id) }
                        )
                    }
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
                            onClick = {
                                showCategoryForm = true; componentMode = ComponentMode.ADD
                            },
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
                        Column(modifier = Modifier.weight(1f)) {
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
                                        showCategoryForm = true
                                        componentMode = ComponentMode.EDIT
                                        clickedCategoryID = categoryID
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(categories[categoryID]?.color ?: 0)
                                    )
                                ) {
                                    Text(stringResource(R.string.edit))
                                }
                                Spacer(Modifier.padding(MaterialTheme.spacing.small))
                            }
                        }

                        Column {
                            categories.forEach { (categoryID, category) ->
                                Button(
                                    onClick = {
                                        clickedCategoryID = categoryID
                                        if (viewModel.validateIDForDeletion(categoryID))
                                            showDeleteDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(categories[categoryID]?.color ?: 0)
                                    )
                                ) {
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
