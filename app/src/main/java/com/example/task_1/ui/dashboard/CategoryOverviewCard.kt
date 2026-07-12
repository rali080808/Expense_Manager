package com.example.task_1.ui.dashboard

import android.icu.math.BigDecimal
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.task_1.domain.Category
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.theme.spacing

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.task_1.ui.PeriodFilter
import com.example.task_1.ui.theme.height
import com.example.task_1.ui.theme.width

@Composable
fun CategoryOverviewCard(
    category: Category,
    totalExpenses: BigDecimal,
    transactions: List<Transaction>,
    periodFilter: PeriodFilter,
    startDate: String,
    endDate: String
) {
    val percentage = category.percentage(totalExpenses, transactions, periodFilter, startDate, endDate)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = category.icon, style = MaterialTheme.typography.displaySmall)

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

        LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .weight(1f)
                .height(MaterialTheme.height.default)
                .clip(MaterialTheme.shapes.small),
            color = Color(category.color),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.width(MaterialTheme.spacing.medium))

        Text(
            text = "${percentage.toInt()}%",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.width(MaterialTheme.width.default)
        )
    }
}