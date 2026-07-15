package com.example.task_1.ui.dashboard

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.task_1.R
import com.example.task_1.domain.ComponentMode
import com.example.task_1.domain.uiStates.DashboardUiState
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.domain.PeriodFilter
import com.example.task_1.domain.getById
import com.example.task_1.domain.uiStates.CategoryUiState
import com.example.task_1.ui.ErrorDialog
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.TransactionCard
import com.example.task_1.ui.theme.spacing
import com.example.task_1.ui.transaction.TransactionForm
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    // val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    var uiState by remember { mutableStateOf<DashboardUiState>(DashboardUiState.Loading) }
    LaunchedEffect(viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect { state ->
                uiState = state
            }
        }
    }

    val isRefreshing = uiState is DashboardUiState.Loading

    var showDescription by remember { mutableStateOf(false) }
    var clickedTransaction by remember { mutableStateOf<Long?>(null) }


    PullToRefreshBox(
        isRefreshing = isRefreshing, onRefresh = { viewModel.loadData() }) {
        when (val state = uiState) {
            is DashboardUiState.Loading -> LoadingScreen()
            is DashboardUiState.Error -> ErrorDialog(
                (uiState as DashboardUiState.Error).message,
                loadData = { viewModel.loadData() }
            )

            is DashboardUiState.Success -> {
                val transactions = state.transactions
                val totalExpenses = state.totalExpenses
                val biggestExpense = state.biggestExpense
                val categories = state.categories

                val today = state.today
                if (showDescription) {
                    ModalBottomSheet(
                        onDismissRequest = { showDescription = false },
                        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                    ) {
                        TransactionForm(
                            currentTransaction = transactions?.getById(clickedTransaction),
                            categories = categories ?: listOf(),
                            actionOnClick = null,
                            errors = null,
                            componentMode = ComponentMode.DETAILS
                        )
                    }
                }
                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = MaterialTheme.spacing.small)
                ) {
                    item {
                        Text(
                            stringResource(R.string.dashboard),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            stringResource(R.string.total),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(MaterialTheme.spacing.small)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = MaterialTheme.spacing.medium),
                            horizontalArrangement = Arrangement.spacedBy(
                                MaterialTheme.spacing.medium, Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SummaryCard(
                                stringResource(R.string.total_sum),
                                totalExpenses.toString()
                            )
                            SummaryCard(
                                stringResource(R.string.biggest_expense), biggestExpense ?: "0.0"
                            )
                        }

                        Text(
                            stringResource(R.string.recent_transactions),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(MaterialTheme.spacing.small)
                        )
                    }

                    items(transactions, key = { it.id ?: -1L }) { transaction ->
                        TransactionCard(
                            transaction,
                            categories.getById(transaction.categoryID) ?: ErrorCategory,
                            onEdit = null,
                            onShowDescription = {
                                showDescription = true
                                clickedTransaction = transaction.id
                            },
                            onDeleteButtonClicked = null
                        )
                    }
                    item {
                        Text(
                            stringResource(R.string.categories_overview),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(top = MaterialTheme.spacing.small)
                        )


                        Column {
                            viewModel.sortCategoriesByExpenseDesc().forEach { category ->
                                CategoryOverviewCard(
                                    category,
                                    totalExpenses,
                                    transactions ,
                                    PeriodFilter.MONTH,
                                    today,
                                    today
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}