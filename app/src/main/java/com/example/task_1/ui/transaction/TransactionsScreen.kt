package com.example.task_1.ui.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.task_1.R
import com.example.task_1.domain.ComponentMode
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.domain.NoFilter
import com.example.task_1.domain.getById
import com.example.task_1.domain.uiStates.DashboardUiState
import com.example.task_1.domain.uiStates.TransactionUiState
import com.example.task_1.ui.ErrorDialog
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.TransactionCard
import com.example.task_1.ui.DeleteDialog
import com.example.task_1.ui.PeriodFilterBar
import com.example.task_1.ui.ShowEmpty
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionViewModel,
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    var uiState by remember { mutableStateOf<TransactionUiState>(TransactionUiState.Loading) }
    LaunchedEffect(viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect { state ->
                uiState = state
            }
        }
    }

    var expandedSortTypes by remember { mutableStateOf(false) }
    var expandedCategoryFilter by remember { mutableStateOf(false) }
    val categoryFilter = remember { NoFilter }
    var lastDate: String? = null
    val scope = rememberCoroutineScope()
    var showForm by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val isRefreshing = uiState is TransactionUiState.Loading
    var componentMode by remember { mutableStateOf(ComponentMode.ADD) }


    var clickedTransactionID by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(uiState) {
        if (uiState is TransactionUiState.Success && (uiState as? TransactionUiState.Success)?.sent == true) {
            showForm = false
        }
    }
    if (showDeleteDialog) {
        DeleteDialog(
            iDForDeletion = clickedTransactionID,
            closeDialog = { showDeleteDialog = false },
            removeObject = { id -> viewModel.deleteTransaction(id) }
        )
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData() }
    ) {
        when (val currentState = uiState) {
            TransactionUiState.Loading -> LoadingScreen()
            is TransactionUiState.Error -> ErrorDialog(
                message = (uiState as TransactionUiState.Error).message,
                loadData = { viewModel.loadData() })
            is TransactionUiState.Empty -> ShowEmpty(R.string.empty_you_have_not_added_transactions_yet_click_the_button_to_add_a_new_transaction)
            is TransactionUiState.Success -> {
                val categories = currentState.categories
                val transactions = currentState.transactions
                val errors = currentState.errors
                val periodFilter = currentState.periodFilter
                val startDate = currentState.startDate
                val endDate = currentState.endDate
                if (showForm) {
                    ModalBottomSheet(
                        onDismissRequest = { showForm = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ) {
                        TransactionForm(
                            currentTransaction = if (componentMode == ComponentMode.ADD)
                                null else transactions.getById(clickedTransactionID),
                            categories = categories,
                            actionOnClick = { transaction ->
                                viewModel.putFormFillingState()
                                if (componentMode == ComponentMode.ADD)
                                    viewModel.addTransaction(transaction)
                                else if (componentMode == ComponentMode.EDIT)
                                    viewModel.editTransaction(transaction)
                                else null
                            },
                            errors = errors,
                            componentMode = componentMode
                        )
                    }
                }
                LazyColumn(Modifier.padding(horizontal = MaterialTheme.spacing.small)) {
                    item {

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                stringResource(R.string.transactions),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Button(
                                onClick = {
                                    if (categories.isEmpty()) viewModel.showError(com.example.task_1.R.string.add_a_category_to_become_able_to_add_transactions)
                                    else {
                                        componentMode = ComponentMode.ADD
                                        showForm = true
                                    }
                                }, Modifier
                                    .clip(MaterialTheme.shapes.small),
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    stringResource(com.example.task_1.R.string.plus_sign),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)) {
                                Box {
                                    Text(
                                        text = if (categoryFilter == NoFilter) stringResource(com.example.task_1.R.string.filter_categories) else stringResource(
                                            com.example.task_1.R.string.filter_cat,
                                            categoryFilter
                                        ),
                                        modifier = Modifier
                                            .clickable {
                                                expandedCategoryFilter = !expandedCategoryFilter
                                            }
                                            .border(
                                                BorderStroke(
                                                    width = MaterialTheme.border.small,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                            .background(color = MaterialTheme.colorScheme.secondary)
                                            .padding(MaterialTheme.spacing.small),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    DropdownMenu(
                                        expanded = expandedCategoryFilter,
                                        onDismissRequest = { expandedCategoryFilter = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text(stringResource(com.example.task_1.R.string.all)) },
                                            onClick = {
                                                scope.launch {
                                                    viewModel.filterByCategory(NoFilter)
                                                    expandedCategoryFilter = false
                                                }
                                            }
                                        )
                                        categories.forEach { filter ->
                                            filter.id?.let { id ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            filter.text + " " + filter.icon,
                                                            color = Color(filter.color)
                                                        )
                                                    },
                                                    onClick = {
                                                        scope.launch {
                                                            viewModel.filterByCategory(id)
                                                            expandedCategoryFilter = false
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }

                                Box {
                                    Text(
                                        text = stringResource(com.example.task_1.R.string.sort_transactions),
                                        modifier = Modifier
                                            .clickable { expandedSortTypes = !expandedSortTypes }
                                            .border(
                                                BorderStroke(
                                                    width = MaterialTheme.border.small,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            )
                                            .background(color = MaterialTheme.colorScheme.secondary)
                                            .padding(MaterialTheme.spacing.small),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    DropdownMenu(
                                        expanded = expandedSortTypes,
                                        onDismissRequest = { expandedSortTypes = false }
                                    ) {
                                        SortTypes.entries.forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option.displayName) },
                                                onClick = {
                                                    viewModel.sortTransactions(option)
                                                    expandedSortTypes = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        PeriodFilterBar(
                            periodFilter = periodFilter,
                            startDate = startDate,
                            endDate = endDate,
                            onPeriodSelected = { periodFilter, startDate, endDate ->
                                viewModel.setDateRange(
                                    periodFilter,
                                    startDate,
                                    endDate
                                )
                            }
                        )
                        Spacer(Modifier.padding(MaterialTheme.spacing.small))
                        Column {
                            viewModel.groupByDate().forEachIndexed { index, transactions ->
                                if (viewModel.currentSortType == SortTypes.SORTBY_DATE_ASCENDING || viewModel.currentSortType == SortTypes.SORTBY_DATE_DESCENDING)
                                    if (transactions.isNotEmpty() && lastDate != transactions[0].date) {
                                        Text(transactions[0].date)
                                        lastDate = transactions[0].date
                                    }

                                transactions.forEach { transaction ->
                                    TransactionCard(
                                        transaction = transaction,
                                        category = categories.getById(transaction.categoryID),
                                        onEdit = {
                                            componentMode = ComponentMode.EDIT
                                            clickedTransactionID = transaction.id
                                            showForm = true
                                        },
                                        onShowDescription = {
                                            componentMode = ComponentMode.DETAILS
                                            showForm = true
                                            clickedTransactionID = transaction.id
                                        },
                                        onDeleteButtonClicked = {
                                            showDeleteDialog = true
                                            clickedTransactionID = transaction.id
                                        }
                                    )
                                }
                                if (index == transactions.size - 1) lastDate = null
                            }
                        }
                    }
                }
            }
            }
        }

    }



