package com.example.task_1.ui.transaction

import android.R
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.domain.MAX_MONEY_LENGTH
import com.example.task_1.domain.MAX_RECEIVER_LENGTH
import com.example.task_1.domain.NoFilter
import com.example.task_1.domain.PayMethod
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.TransactionUiState
import com.example.task_1.domain.Transactions
import com.example.task_1.ui.ErrorDialog
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.TransactionCard
import com.example.task_1.ui.theme.ErrorColor
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    modifier: Modifier,
    style: TextStyle,
    viewModel: TransactionViewModel,
    onAddClick: () -> Unit,
    onNavigateToDescription: (String, () -> Unit) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    var expandedSortTypes by remember { mutableStateOf(false) }
    var expandedCategoryFilter by remember { mutableStateOf(false) }
    val categoryFilter = remember { NoFilter }
    val isRefreshing = uiState is TransactionUiState.Loading
    var lastDate: String? = null
    val scope = rememberCoroutineScope()
    var showAddTransactionSheet by remember { mutableStateOf(false) }
    val categories = (uiState as? TransactionUiState.Success)?.categories ?: mapOf()
    val transactions = (uiState as? TransactionUiState.Success)?.transactions ?: listOf()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData() }
    ) {


        if (uiState is TransactionUiState.Loading) LoadingScreen()
        if (uiState is TransactionUiState.Error) {
            ErrorDialog(
                message = (uiState as TransactionUiState.Error).message ,
                args = (uiState as TransactionUiState.Error).args,
                loadData = { viewModel.loadData() })
        }

        if (showAddTransactionSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddTransactionSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                AddTransaction(
                    returnToTransactionScreen = {
                        showAddTransactionSheet = false;
                        viewModel.loadData()
                    },
                    categories = categories,
                    addTransaction = { transaction -> viewModel.addTransaction(transaction) },

                    )
            }
        }
        LazyColumn(Modifier.padding(start = MaterialTheme.spacing.medium)) {
            item {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Transactions",
                        modifier = modifier,
                        style = style,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = {
                            if (categories.isEmpty()) viewModel.showError(com.example.task_1.R.string.add_a_category_to_become_able_to_add_transactions)
                            else showAddTransactionSheet = true

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
                                categories.forEach { (id, filter) ->
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
                Spacer(Modifier.padding(MaterialTheme.spacing.small))
                Column {
                    transactions.forEachIndexed { index, transaction ->
                        if (viewModel.currentSortType == SortTypes.SORTBY_DATE_ASCENDING || viewModel.currentSortType == SortTypes.SORTBY_DATE_DESCENDING)
                            if (lastDate != transaction.date)
                                Text(transaction.date)
                        TransactionCard(
                            transaction = transaction,
                            category = categories[transaction.categoryID] ?: ErrorCategory,
                            showDescription = onNavigateToDescription
                        )
                        lastDate = transaction.date
                        if (index == transactions.size - 1) lastDate =
                            null
                    }
                }
            }
        }
    }
}


@Composable
fun AddTransaction(
    returnToTransactionScreen: () -> Unit,
    categories: Map<Int, Category>,
    addTransaction: (Transaction) -> Unit
) {
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPayMethod by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var sender by remember { mutableStateOf("") }

    var receiver by remember { mutableStateOf("") }
    var sum by remember { mutableStateOf("0.0") }
    var categoryID by remember { mutableIntStateOf(NoFilter) }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var description by remember { mutableStateOf("") }
    var payMethod by remember { mutableStateOf(PayMethod.DEBIT) }

    val datePickerState = rememberDatePickerState()

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
        Text("New Transaction", style = MaterialTheme.typography.titleLarge)


        OutlinedTextField(
            value = sender,
            onValueChange = { if (it.length < MAX_RECEIVER_LENGTH) sender = it },
            label = { Text(stringResource(com.example.task_1.R.string.sender)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = receiver,
            onValueChange = { if (it.length < MAX_RECEIVER_LENGTH) receiver = it },
            label = { Text(stringResource(com.example.task_1.R.string.receiver)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = sum,
            onValueChange = { if (it.length < MAX_MONEY_LENGTH) sum = it },
            singleLine = true,
            label = { Text(stringResource(com.example.task_1.R.string.money)) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = date.toString(),
                onValueChange = { },
                label = { Text(stringResource(com.example.task_1.R.string.date)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    }) { Text(stringResource(R.string.ok)) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) { Text(stringResource(com.example.task_1.R.string.cancel)) }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }


        Row { // TODO here it shows null when nofilter

            val category = categories[categoryID]

            if (category != null) {
                Text(
                    text = stringResource(
                        com.example.task_1.R.string.category,
                        category.text,
                        category.icon
                    )
                )
            }
            IconButton(onClick = { expandedCategory = !expandedCategory }) {
                Icon(
                    androidx.compose.ui.res.painterResource(id = com.example.task_1.R.drawable.ic_home),
                    contentDescription = stringResource(com.example.task_1.R.string.select_category)
                )
            }
        }

        DropdownMenu(
            expanded = expandedCategory,
            onDismissRequest = { expandedCategory = false }
        ) {
            categories.forEach { (id, option) ->
                DropdownMenuItem(
                    text = { Text(option.text + " " + option.icon, color = Color(option.color)) },
                    onClick = {
                        categoryID = id
                        expandedCategory = false
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = payMethod.name,
                onValueChange = { },
                label = { Text(stringResource(com.example.task_1.R.string.payment_method)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expandedPayMethod = true }
            )
        }

        DropdownMenu(
            expanded = expandedPayMethod,
            onDismissRequest = { expandedPayMethod = false }
        ) {
            PayMethod.entries.forEach { method ->
                DropdownMenuItem(
                    text = { Text(method.name) },
                    onClick = {
                        payMethod = method
                        expandedPayMethod = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(com.example.task_1.R.string.description)) },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val amount = sum.toDoubleOrNull() ?: 0.0
                addTransaction(
                    Transaction(
                        sender = sender,
                        receiver = receiver,
                        money = amount,
                        date = date.toString(),
                        categoryID = categoryID,
                        description = description,
                        payMethod = payMethod
                    )
                )
                if (categoryID != NoFilter) returnToTransactionScreen()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(com.example.task_1.R.string.save))
        }
    }
}
