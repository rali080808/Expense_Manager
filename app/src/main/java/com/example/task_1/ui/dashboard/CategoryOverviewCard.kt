package com.example.task_1.ui.dashboard

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.task_1.domain.Category
import com.example.task_1.ui.theme.spacing

@Composable
fun CategoryOverviewCard(category: Category, totalExpenses: Double) {
    Text(
        (category.icon)
                + " "
                + "🟦".repeat(category.percentage(totalExpenses) * 16 / 100)
                + category.percentage(totalExpenses)
                + "%",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(MaterialTheme.spacing.small)
    )
}