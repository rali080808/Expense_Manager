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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.task_1.domain.Category
import com.example.task_1.domain.MAX_MONEY_LENGTH
import com.example.task_1.domain.MAX_RECEIVER_LENGTH
import com.example.task_1.domain.NoFilter
import com.example.task_1.domain.PayMethod
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.TransactionInput
import com.example.task_1.ui.theme.border
import com.example.task_1.ui.theme.spacing
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach


@Composable
fun TransactionForm(
    categories: Map<Int, Category>,
    actionOnClick: (TransactionInput) -> Unit
) {
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPayMethod by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var sender by remember { mutableStateOf("") }

    var receiver by remember { mutableStateOf("") }
    var sum by remember { mutableStateOf("0.0") }
    var categoryID by remember { mutableIntStateOf(NoFilter) }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var description by remember { mutableStateOf("") }
    var payMethod by remember { mutableStateOf(PayMethod.DEBIT) }

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
            onValueChange = { if (it.length < MAX_RECEIVER_LENGTH) sender = it },
            label = { Text(stringResource(com.example.task_1.R.string.sender)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = receiver,
            onValueChange = { if (it.length < MAX_RECEIVER_LENGTH) receiver = it },
            label = { Text(stringResource(com.example.task_1.R.string.receiver)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = sum,
            onValueChange = { if (it.length < MAX_MONEY_LENGTH) sum = it },
            singleLine = true,
            label = { Text(stringResource(com.example.task_1.R.string.money)) },
            modifier = Modifier.fillMaxWidth()
        )

        //  Spacer(modifier = Modifier.height(MaterialTheme.spacing.small))

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = date.toString(),
                onValueChange = { },
                label = { Text(stringResource(com.example.task_1.R.string.date)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
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
                }) { Text(stringResource(com.example.task_1.R.string.ok)) }
            }, dismissButton = {
                TextButton(onClick = {
                    showDatePicker = false
                }) { Text(stringResource(com.example.task_1.R.string.cancel)) }
            }) {
                DatePicker(state = datePickerState)
            }
        }
// category
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = categories[categoryID]?.text ?: "",
                onValueChange = { },
                label = { Text(stringResource(com.example.task_1.R.string.select_category)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    if (categories[categoryID] != null) {
                        Text(
                            text = categories[categoryID]!!.icon,
                            style = MaterialTheme.typography.titleMedium
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
                categories.forEach { (id, option) ->
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
                    })
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = payMethod.text,
                onValueChange = { },
                label = { Text(stringResource(com.example.task_1.R.string.payment_method)) },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expandedPayMethod = true })


            DropdownMenu(
                expanded = expandedPayMethod, onDismissRequest = { expandedPayMethod = false }) {
                PayMethod.entries.forEach { method ->
                    DropdownMenuItem(text = { Text(method.text) }, onClick = {
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
                val amount = sum.toDoubleOrNull() ?: 0.0
                actionOnClick(
                    TransactionInput(
                        sender = sender,
                        receiver = receiver,
                        money = amount,
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