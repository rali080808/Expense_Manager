package com.example.task_1.ui.dashboard

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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.task_1.R
import com.example.task_1.domain.Category
import com.example.task_1.domain.ComponentMode
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.getById
import com.example.task_1.ui.TransactionCard
import com.example.task_1.ui.theme.spacing
import com.example.task_1.ui.transaction.TransactionForm
import java.math.BigDecimal
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    transactions: List<Transaction>,
    totalExpenses: String,
    biggestExpense: String,
    categories: List<Category>,
) {
    var showDescription by remember { mutableStateOf(false) }
    var clickedTransaction by remember { mutableStateOf<Long?>(null) }
    if (showDescription) {
        ModalBottomSheet(
            onDismissRequest = { showDescription = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            TransactionForm(
                currentTransaction = transactions.getById(clickedTransaction),
                categories = categories,
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
                SummaryCard(stringResource(R.string.total_sum), totalExpenses)
                SummaryCard(
                    stringResource(R.string.biggest_expense), biggestExpense
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
                        categories.getById(transaction.categoryID) ?: ErrorCategory,
                        onEdit = null,
                        onShowDescription = {
                            showDescription = true
                            clickedTransaction = transaction.id
                        }
                    )
                }
            }
            Text(
                stringResource(R.string.categories_overview),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = MaterialTheme.spacing.small)
            )


            Column {
                categories.forEach { category ->
                    CategoryOverviewCard(category, totalExpenses, transactions)
                }
            }
        }
    }
}