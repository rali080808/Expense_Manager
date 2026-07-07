package com.example.task_1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.task_1.R
import com.example.task_1.domain.Category
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.theme.Money
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing


@Composable
fun TransactionCard(
    transaction: Transaction,
    category: Category,
) {
    var activeDescriptionDialog by remember { mutableStateOf(false) }

    if (activeDescriptionDialog)
        ShowDescription(
            description = transaction.description,
            onClose = { activeDescriptionDialog = false }
        )

    Card(
        modifier = Modifier
            //.fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.medium)
            .clickable(onClick = { activeDescriptionDialog = true }),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = MaterialTheme.spacing.small
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.small),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium)
            ) {
                Text(
                    text = category.icon,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer, shape = CircleShape
                        )
                        .padding(MaterialTheme.spacing.medium),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = buildAnnotatedString {
                        append(transaction.sender)

                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(" ➔ ")
                        }

                        append(transaction.receiver)
                    },
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                "${transaction.money} ${transaction.currency.sign}",
                style = MaterialTheme.typography.headlineSmall,
                color = Money
            )
        }

        Text(
            transaction.sender + " " + stringResource(R.string.gave) + " " + transaction.money + " " + transaction.currency + " " + stringResource(
                R.string.to
            ) + " " + transaction.receiver + " " + stringResource(R.string.on) + " " + transaction.date + ".",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,

            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.spacing.medium,
                    end = MaterialTheme.spacing.medium,
                    bottom = MaterialTheme.spacing.medium
                )
        )
    }
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

}


@Composable
fun ShowDescription(
    description: String, onClose: () -> Unit
) {

    Dialog(
        onDismissRequest = { onClose() },
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
                    text = stringResource(R.string.description),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

                Text(
                    text = description, style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))

                Text(
                    text = stringResource(R.string.dismiss),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onClose() })
            }
        }
    }


}