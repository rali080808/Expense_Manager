package com.example.task_1.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import com.example.task_1.domain.CategoriesOverview
import com.example.task_1.domain.Transactions

@Composable
fun CategoryOverviewCard(categoriesOverview: CategoriesOverview, transactions : Transactions) {
    Text( categoriesOverview.category.icon + " " + "🟦".repeat(categoriesOverview.percentage(transactions.totalExpenses())*16/100)  + categoriesOverview.percentage(transactions.totalExpenses()) + "%" , fontSize = 20.sp )
}// TODO progress bar