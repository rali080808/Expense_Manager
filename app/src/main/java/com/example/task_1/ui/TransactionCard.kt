package com.example.task_1.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.theme.Money
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing


@Composable
//                    TransactionCard(index, getTransaction, transaction.categoryID, getCategory,onNavigateToDescription
fun TransactionCard(
    transaction: Transaction,
    category: Category,
    showDescription: (String) -> Unit,
) {

    Column(
        modifier = Modifier
            .border(
                width = MaterialTheme.border.medium,
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primary
            )
            .clickable(onClick = { showDescription(transaction.description) })
    ) {
        Row {
            Text(
                category.icon, style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(MaterialTheme.spacing.medium)
            )
            Text(
                transaction.sender
                        + " -> "
                        + transaction.receiver,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(MaterialTheme.spacing.medium)
            )
            Text(
                "" + transaction.money
                        + " "
                        + transaction.currency,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(
                    start = MaterialTheme.spacing.large,
                    end = MaterialTheme.spacing.medium,
                    top = MaterialTheme.spacing.medium
                ),
                color = Money
            )
        }
        Text(
            transaction.sender
                    + " gave "
                    + transaction.money
                    + " "
                    + transaction.currency
                    + " to "
                    + transaction.receiver
                    + " on "
                    + transaction.date.toString()
                    + ".",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(MaterialTheme.spacing.medium)
        )
    }
}

@Composable
fun ShowDescription(
    description: String,
    returnToMainScreen: () -> Unit
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Text(description)
            Button(onClick = returnToMainScreen) {
                Text("Return")
            }
        }
    }
}