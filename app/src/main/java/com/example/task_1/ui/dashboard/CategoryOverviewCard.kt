package com.example.task_1.ui.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.task_1.domain.CategoriesOverview
import com.example.task_1.domain.Transactions
import com.example.task_1.ui.theme.spacing
@Composable
fun CategoryOverviewCard(categoriesOverview: CategoriesOverview, transactions : Transactions) {
    Text( categoriesOverview.category.icon
            + " "
            + "🟦".repeat(categoriesOverview.percentage(transactions.totalExpenses())*16/100)
            + categoriesOverview.percentage(transactions.totalExpenses())
            + "%" ,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(MaterialTheme.spacing.small))
}