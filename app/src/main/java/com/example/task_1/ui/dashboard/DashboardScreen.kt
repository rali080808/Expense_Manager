package com.example.task_1.ui.dashboard
import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit

import com.example.task_1.data.transactions
import com.example.task_1.domain.CategoriesOverview
import com.example.task_1.ui.theme.spacing


@Composable
fun DashboardScreen(modifier : Modifier, style: TextStyle) {
    LazyColumn(Modifier.padding(start = MaterialTheme.spacing.medium)) {
    item{    Text("Dashboard", modifier = modifier
            , style = style, color = MaterialTheme.colorScheme.primary
        )
        Text("Total", fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic,
            modifier= Modifier.padding(MaterialTheme.spacing.small))

        Row( modifier = Modifier.fillMaxWidth().padding(end=MaterialTheme.spacing.medium),
            horizontalArrangement  = Arrangement.spacedBy(MaterialTheme.spacing.medium, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically) {
            SummaryCard("Total sum", transactions.totalExpenses())
            SummaryCard("Biggest expense", transactions.transactions[transactions.indexOfBiggestExpense()].money)
        }

        Text("Recent Transactions", fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic,
            modifier= Modifier.padding(MaterialTheme.spacing.small))

        Column() {
           transactions.transactions.forEach {
                   transaction ->
               TransactionCard(transaction)
           }
        }
        Text("Categories Overview", fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            fontStyle = FontStyle.Italic,
            modifier= Modifier.padding(top=MaterialTheme.spacing.small))

        val categoryExpenses: List<CategoriesOverview> = transactions.categoriesOverview();
        Column {
            categoryExpenses.forEach { item ->
                CategoryOverviewCard(item, transactions)
            }
        }
    }}
}