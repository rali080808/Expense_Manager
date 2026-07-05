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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.task_1.R
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.Transactions
import com.example.task_1.ui.TransactionCard
import com.example.task_1.ui.theme.spacing
import kotlin.collections.forEach

@Composable
fun DashboardContent(
    modifier: Modifier,
    style: TextStyle,
    transactions: List<Transaction>,
    totalExpenses: Double,
    biggestExpense: Double,
    categories: Map<Int, Category>,
    onNavigateToDescription: (String, ()->Unit) -> Unit,
) {
    LazyColumn(Modifier
        .fillMaxSize()
        .padding(start = MaterialTheme.spacing.medium)) {
        item {
            Text(
                stringResource(R.string.dashboard),
                modifier = modifier,
                style = style,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                stringResource(R.string.total), fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(MaterialTheme.spacing.small)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = MaterialTheme.spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.medium,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SummaryCard(stringResource(R.string.total_sum), totalExpenses)
                SummaryCard(
                    stringResource(R.string.biggest_expense),
                    biggestExpense
                )
            }

            Text(
                stringResource(R.string.recent_transactions),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(MaterialTheme.spacing.small)
            )

            Column() {
                transactions.forEachIndexed { index, transaction ->
                    TransactionCard(
                        transaction,
                        categories[transaction.categoryID]?: ErrorCategory,
                        onNavigateToDescription
                    )
                }
            }
            Text(
                stringResource(R.string.categories_overview), fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = MaterialTheme.spacing.small)
            )


            Column {
                categories.forEach { (_, category) ->
                    CategoryOverviewCard(category, totalExpenses)
                }
            }
        }
    }
}