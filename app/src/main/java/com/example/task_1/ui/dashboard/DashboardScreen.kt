package com.example.task_1.ui.dashboard
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import com.example.task_1.domain.CategoriesOverview
import com.example.task_1.domain.UiState
import com.example.task_1.ui.theme.spacing
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.task_1.domain.Category
import com.example.task_1.ui.dashboard.DashboardViewModel
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.TransactionCard

@Composable
fun DashboardScreen(modifier : Modifier, style: TextStyle, viewModel: DashboardViewModel, onNavigateToDescription: (String) -> Unit) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()


    val uiState by viewModel.uiState.collectAsState()
    val isRefreshing = uiState is UiState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.loadData() }
    ) {
        when (uiState) {
            UiState.Loading -> LoadingScreen()
            is UiState.Error -> Text("error")
            is UiState.Success<*> -> LazyColumn(Modifier.fillMaxSize().padding(start = MaterialTheme.spacing.medium)) {
                item {
                    Text(
                        "Dashboard",
                        modifier = modifier,
                        style = style,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "Total", fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(MaterialTheme.spacing.small)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(end = MaterialTheme.spacing.medium),
                        horizontalArrangement = Arrangement.spacedBy(
                            MaterialTheme.spacing.medium,
                            Alignment.CenterHorizontally
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SummaryCard("Total sum", transactions.totalExpenses())
                        SummaryCard(
                            "Biggest expense",
                            transactions.getBiggestExpense()
                        )
                    }

                    Text(
                        "Recent Transactions", fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(MaterialTheme.spacing.small)
                    )

                    Column() {
                        transactions.getTransactions().forEach { transaction ->
                            TransactionCard(transaction, onNavigateToDescription, categories[transaction.categoryID] ?: Category(
                                "developer bug: transaction with a wrong category id",
                                "",
                                Color.Red
                            )
                            )
                        }
                    }
                    Text(
                        "Categories Overview", fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(top = MaterialTheme.spacing.small)
                    )

                    val categoryExpenses: List<CategoriesOverview> =
                        viewModel.categoriesOverview();
                    Column {
                        categoryExpenses.forEach { item ->
                            CategoryOverviewCard(item, viewModel)
                        }
                    }
                }
            }
        }

    }
}