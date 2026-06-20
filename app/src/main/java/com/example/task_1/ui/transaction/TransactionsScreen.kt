package com.example.task_1.ui.transaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.task_1.domain.Category
import com.example.task_1.domain.PayMethod
import com.example.task_1.domain.SortTypes
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.TransactionViewModel
import com.example.task_1.domain.UiState
import com.example.task_1.ui.ErrorScreen
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.TransactionCard
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun TransactionsScreen(
    modifier: Modifier,
    style: TextStyle,
    viewModel: TransactionViewModel,
    onAddClick: () -> Unit,
    onNavigateToDescription: (String) -> Unit
) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    var expandedSortTypes by remember { mutableStateOf(false) }
    var expandedCategoryFilter by remember { mutableStateOf(false) }
    val categoryFilter = remember { Category("all", "all") }
    val isRefreshing = uiState is UiState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData() }
    ) {
        when (uiState) {
            UiState.Loading -> LoadingScreen()
            is UiState.Error -> Text("error")
            is UiState.Success<*> -> {
                LazyColumn(Modifier.padding(start = MaterialTheme.spacing.medium)) {
                    item {
                        Row {
                            Text(
                                "Transactions",
                                modifier = modifier,
                                style = style,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Button(onClick = onAddClick) { Text("+") }

                        }
                        Row {
                            Box {
                                Text(
                                    text = if (categoryFilter.text == "all") "Filter Categories" else "Filter: $categoryFilter",
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
                                        .background(color = MaterialTheme.colorScheme.secondary),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                DropdownMenu(
                                    expanded = expandedCategoryFilter,
                                    onDismissRequest = { expandedCategoryFilter = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("All") },
                                        onClick = {
                                            // TODO const variable of type category for "all"
                                            viewModel.filterByCategory(Category("all", "all"))
                                            expandedCategoryFilter = false
                                        }
                                    )
                                    categories.forEach { filter ->
                                        DropdownMenuItem(
                                            text = { Text(filter.text) },
                                            onClick = {
                                                viewModel.filterByCategory(filter)
                                                expandedCategoryFilter = false
                                            }
                                        )
                                    }
                                }
                            }
                            Box {
                                Text(
                                    text = "Sort Transactions",
                                    modifier = Modifier
                                        .clickable { expandedSortTypes = !expandedSortTypes }
                                        .border(
                                            BorderStroke(
                                                width = MaterialTheme.border.small,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        )
                                        .background(color = MaterialTheme.colorScheme.secondary),
                                    style = MaterialTheme.typography.titleMedium
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
                        Column {
                            transactions.getTransactions().forEach { transaction ->
                                TransactionCard(transaction, onNavigateToDescription)
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun AddTransaction(
    returnToTransactionScreen: () -> Unit,
    viewModel: TransactionViewModel
) {

    val categories by viewModel.categories.collectAsState()

    if (categories.isEmpty()) {
        ErrorScreen("Add a category to become able to add transaactions")
        return;
    }

    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPayMethod by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var receiver by remember { mutableStateOf("") }
    var sum by remember { mutableStateOf("0.0") }
    var category by remember { mutableStateOf(if (categories.isNotEmpty()) categories[0] else null) }
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
            value = receiver,
            onValueChange = { receiver = it },
            label = { Text("Receiver") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = sum,
            onValueChange = { sum = it },
            label = { Text("Money") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = date.toString(),
                onValueChange = { },
                label = { Text("Date") },
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
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }


        Row {
            Text("Category: ${category?.text}  ${category?.icon} ")
            IconButton(onClick = { expandedCategory = !expandedCategory }) {
                Icon(
                    androidx.compose.ui.res.painterResource(id = com.example.task_1.R.drawable.ic_home),
                    contentDescription = "Select Category"
                )
            }
        }

        DropdownMenu(
            expanded = expandedCategory,
            onDismissRequest = { expandedCategory = false }
        ) {
            categories.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.text + " " + option.icon) },
                    onClick = {
                        category = option
                        expandedCategory = false
                    }
                )
            }
        }


        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = payMethod.name,
                onValueChange = { },
                label = { Text("Payment Method") },
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
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                val amount = sum.toDoubleOrNull() ?: 0.0
                category?.let { cat ->
                    viewModel.addTransaction(
                        Transaction(
                            sender = "Me",
                            receiver = receiver,
                            money = amount,
                            date = date,
                            category = cat,
                            description = description,
                            payMethod = payMethod
                        )
                    )
                    returnToTransactionScreen()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
