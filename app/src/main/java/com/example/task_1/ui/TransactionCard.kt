package com.example.task_1.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
 import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.ViewModel
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.theme.Money
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing


@Composable
fun TransactionCard(
    transaction: Transaction,
    category: Category,
    showDescription: (String, ()->Unit) -> Unit,
) {
    var activeDescriptionDialog by remember { mutableStateOf(false) }
    if (activeDescriptionDialog) showDescription(transaction.description, {activeDescriptionDialog=false})

    Column(
        modifier = Modifier
            .border(
                width = MaterialTheme.border.medium,
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primary
            )
            .clickable(onClick = { activeDescriptionDialog = true })
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
                        + transaction.currency.sign,
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
                    + transaction.date
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

    Dialog(onDismissRequest = { returnToMainScreen() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
         Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
             modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.large)
        ) {
            Column(modifier = Modifier.padding(MaterialTheme.spacing.medium)) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                Text(
                    text = "Dismiss",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { returnToMainScreen() }
                )
            }
        }
    }

//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Column {
//            Text(description)
//            Button(onClick = returnToMainScreen) {
//                Text("Return")
//            }
//        }
//    }
}