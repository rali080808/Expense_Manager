package com.example.task_1.ui.category

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.task_1.R
import com.example.task_1.domain.Category
import com.example.task_1.domain.uiStates.CategoryUiState
import com.example.task_1.domain.ComponentMode
import com.example.task_1.domain.getById
import com.example.task_1.ui.DeleteDialog
import com.example.task_1.ui.ErrorDialog
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.theme.spacing
import com.example.task_1.ui.theme.width

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(

    viewModel: CategoryViewModel,
    addCategory: (Category) -> Unit,
    editCategory: (Long, Category) -> Unit,
) {
    val context = LocalContext.current

    val uiState by viewModel.uiState.collectAsState()
    val categories = (uiState as? CategoryUiState.Success)?.categories
        ?: (uiState as? CategoryUiState.FormFilling)?.categories
        ?: emptyList()

//    val transactions = (uiState as? CategoryUiState.Success)?.transactions
//        ?: (uiState as? CategoryUiState.FormFilling)?.transactions
//        ?: emptyList()
    val errors = (uiState as? CategoryUiState.FormFilling)?.errors

    val isRefreshing = uiState is CategoryUiState.Loading
    var showForm by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var componentMode by remember { mutableStateOf(ComponentMode.ADD) }
    var clickedCategoryID by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is CategoryUiState.Success) {
            showForm = false
        }
    }
    if (showForm) {
        ModalBottomSheet(
            onDismissRequest = { showForm = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            CategoryForm(
                currentCategory = if (componentMode == ComponentMode.EDIT) categories.getById(
                    clickedCategoryID
                ) else null,
                actionOnClick = { category ->
                    if (componentMode == ComponentMode.ADD)
                        addCategory(category)
                    else {
                        val currentId = clickedCategoryID
                        if (currentId != null) {
                            editCategory(currentId, category)
                        } else {
                            Log.e(
                                "UI_BUG",
                                "Category edit requested, but clickedCategoryID was null!"
                            )

                            Toast.makeText( // TODO lang
                                context,
                                "An error occurred. Please try again.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                    }

                    // showForm = false
                },
                onCancel = { showForm = false },
                errors = errors
            )
        }
    }
    if (showDeleteDialog) {
        clickedCategoryID?.let { clickedCategoryID ->
            val currentCategory = categories.getById(clickedCategoryID)
            currentCategory?.let { currentCategory ->
                DeleteDialog(
                    iDForDeletion = clickedCategoryID,
                    closeDialog = { showDeleteDialog = false },
                    removeObject = { id -> viewModel.removeCategory(id) }
                )
            }
            if (currentCategory == null) {
                Log.e(
                    "UI_ERROR",
                    "Tried to delete category $clickedCategoryID, but it wasn't found in the list!"
                )
                Toast.makeText(
                    context,
                    stringResource(R.string.an_error_occurred_please_try_again),
                    Toast.LENGTH_LONG
                ).show()
                showDeleteDialog = false
            }
        }
        if (clickedCategoryID == null) {
            Log.e(
                "UI_ERROR",
                "Tried to delete category $clickedCategoryID, but it is null!"
            )
            Toast.makeText(
                context,
                stringResource(R.string.an_error_occurred_please_try_again),
                Toast.LENGTH_LONG
            ).show()
            showDeleteDialog = false
        }
    }
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData() }
    ) {
        if (uiState is CategoryUiState.Loading) {
            LoadingScreen()
        }
        if (uiState is CategoryUiState.Error) {
            ErrorDialog(
                message = (uiState as CategoryUiState.Error).message,
//args = (uiState as CategoryUiState.Error).args,
                loadData = { viewModel.loadData() })
        }
        LazyColumn(
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
                        onClick = {
                            showForm = true; componentMode = ComponentMode.ADD
                        },
                        Modifier.clip(MaterialTheme.shapes.small),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("+", style = MaterialTheme.typography.bodyLarge)
                    }
                }
                Spacer(Modifier.padding(MaterialTheme.spacing.medium))
            }
            items(categories) { category ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = MaterialTheme.spacing.small),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.text,
                            modifier = Modifier.width(MaterialTheme.width.small),
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        Text(
                            text = " ${category.icon}",
                            style = MaterialTheme.typography.titleMedium
                        )

                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                showForm = true
                                componentMode = ComponentMode.EDIT
                                clickedCategoryID = category.id
                            },

                            ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color(
                                    categories.getById(category.id)?.color ?: 0
                                )
                            )
                        }

                        IconButton(
                            onClick = {

                                clickedCategoryID = category.id
                                if (viewModel.validateIDForDeletion(category.id))
                                    showDeleteDialog = true
                                else
                                    Toast.makeText(
                                        context,
                                        context.getString(
                                            R.string.category_is_active_you_cannot_delete_it,
                                            category.text,
                                            category.icon
                                        ),
                                        Toast.LENGTH_SHORT
                                    ).show()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Transaction",
                                tint = Color(category.color)
                            )
                        }


                    }

                }
                Spacer(Modifier.padding(MaterialTheme.spacing.extraSmall))
            }
        }
    }
}



