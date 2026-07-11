package com.example.task_1.ui.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.task_1.R
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.NoFilter
import com.example.task_1.domain.PayMethod
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.getById
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.collections.forEach


@Composable
fun TransactionForm(
    currentTransaction: Transaction?,
    categories: List<Category>,
    actionOnClick: (Transaction) -> Unit,
    errors: Map<TransactionFormFields, ErrorMessage>?
) {
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPayMethod by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    var sender by remember { mutableStateOf(currentTransaction?.sender ?: "") }
    var receiver by remember { mutableStateOf(currentTransaction?.receiver ?: "") }
    var sum by remember { mutableStateOf(currentTransaction?.money ?: "0.0") }
    var categoryID by remember { mutableLongStateOf(currentTransaction?.categoryID ?: NoFilter) }
    var date by remember { mutableStateOf(currentTransaction?.date?.let { LocalDate.parse(it) } ?: LocalDate.now()) }
    var description by remember { mutableStateOf(currentTransaction?.description ?: "") }
    var payMethod by remember { mutableStateOf(currentTransaction?.payMethod ?: PayMethod.DEBIT) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MaterialTheme.spacing.medium)
            .clip(MaterialTheme.shapes.medium)
            .border(
                BorderStroke(
                    width = MaterialTheme.border.medium, color = MaterialTheme.colorScheme.primary
                )
            )
            .padding(MaterialTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        Text("New Transaction", style = MaterialTheme.typography.titleMedium)


        OutlinedTextField(
            value = sender,
            onValueChange = { sender = it },
            label = { Text(stringResource(R.string.sender)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = errors?.containsKey(TransactionFormFields.SENDER) == true,
            supportingText = {
                val error = errors?.get(TransactionFormFields.SENDER)
                if (error != null && error.messageID != R.string.empty_string) {
                    Text(
                        text = stringResource(id = error.messageID, *error.args.toTypedArray()),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        OutlinedTextField(
            value = receiver,
            onValueChange = {  receiver = it },
            label = { Text(stringResource(R.string.receiver)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = errors?.containsKey(TransactionFormFields.RECEIVER) == true,
             supportingText = {
                val error = errors?.get(TransactionFormFields.RECEIVER)
                if (error != null && error.messageID != R.string.empty_string) {
                    Text(
                        text = stringResource(id = error.messageID, *error.args.toTypedArray()),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        OutlinedTextField(
            value = sum,
            onValueChange = { sum = it },
            singleLine = true,
            label = { Text(stringResource(R.string.money)) },
            modifier = Modifier.fillMaxWidth(),
            isError = errors?.containsKey(TransactionFormFields.MONEY) == true,
             supportingText = {
                val error = errors?.get(TransactionFormFields.MONEY)
                if (error != null && error.messageID != R.string.empty_string) {
                    Text(
                        text = stringResource(id = error.messageID, *error.args.toTypedArray()),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = date.toString(),
                onValueChange = { },
                label = { Text(stringResource(R.string.date)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                isError = errors?.containsKey(TransactionFormFields.DATE) == true,
                 supportingText = {
                    val error = errors?.get(TransactionFormFields.DATE)
                    if (error != null && error.messageID != R.string.empty_string) {
                        Text(
                            text = stringResource(id = error.messageID, *error.args.toTypedArray()),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDatePicker = true })
        }

        if (showDatePicker) {
            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.ok)) }
            }, dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) { Text(stringResource(R.string.cancel)) }
            }) {
                DatePicker(state = datePickerState)
            }
        }
// category
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = categories.getById(categoryID)?.text ?: "",
                onValueChange = { },
                label = { Text(stringResource(com.example.task_1.R.string.select_category)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                         Text(
                            text = categories.getById(categoryID)?.icon ?: "",
                            style = MaterialTheme.typography.titleMedium
                        )

                },
                isError = errors?.containsKey(TransactionFormFields.CATEGORY) == true,
                 supportingText = {
                    val error = errors?.get(TransactionFormFields.CATEGORY)
                    if (error != null && error.messageID != R.string.empty_string) {
                        Text(
                            text = stringResource(id = error.messageID, *error.args.toTypedArray()),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expandedCategory = !expandedCategory })


            DropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surface),
                offset = androidx.compose.ui.unit.DpOffset(0.dp, 0.dp) // This anchors it to the Box

            ) {
                categories.forEach { option  ->
                    option.id?.let{ id ->
                    DropdownMenuItem(text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = option.icon, style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = option.text,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(option.color)
                            )
                        }
                    }, onClick = {
                        categoryID = id
                        expandedCategory = false
                    })}

                }
            }

        }

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = stringResource(payMethod.text),
                onValueChange = { },
                label = { Text(stringResource(com.example.task_1.R.string.payment_method)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                isError = errors?.containsKey(TransactionFormFields.PAY_METHOD) == true,
                 supportingText = {
                    val error = errors?.get(TransactionFormFields.PAY_METHOD)
                    if (error != null && error.messageID != R.string.empty_string) {
                        Text(
                            text = stringResource(id = error.messageID, *error.args.toTypedArray()),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expandedPayMethod = true })


            DropdownMenu(
                expanded = expandedPayMethod, onDismissRequest = { expandedPayMethod = false }) {
                PayMethod.entries.forEach { method ->
                    if ( method != PayMethod.UNKNOWN)
                    DropdownMenuItem(text = { Text(stringResource(method.text)) }, onClick = {
                        payMethod = method
                        expandedPayMethod = false
                    })
                }
            }
        }

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(stringResource(com.example.task_1.R.string.description)) },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                 actionOnClick(
                    Transaction(
                        id=currentTransaction?.id  ,
                        sender = sender,
                        receiver = receiver,
                        money = sum,
                        date = date.toString(),
                        categoryID = categoryID,
                        description = description,
                        payMethod = payMethod
                    )
                )
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(com.example.task_1.R.string.save))
        }
    }
}