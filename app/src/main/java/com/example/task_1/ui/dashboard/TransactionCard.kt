package com.example.task_1.ui.dashboard

import androidx.compose.animation.core.animate
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.theme.Money
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing

@Composable
fun TransactionCard(transaction: Transaction){
    Column(modifier = Modifier.border(width = MaterialTheme.border.medium, shape= MaterialTheme.shapes.large,
        color= MaterialTheme.colorScheme.primary)){
        Row {
            Text(transaction.sender
                    + " -> "
                    + transaction.receiver,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(MaterialTheme.spacing.medium))
            Text("" + transaction.money
                    + " "
                    + transaction.currency,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(start=MaterialTheme.spacing.large, end=MaterialTheme.spacing.medium, top=MaterialTheme.spacing.medium) ,
                color= Money)
        }
        Text(transaction.sender
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
            modifier = Modifier.padding(MaterialTheme.spacing.medium))
    }
}