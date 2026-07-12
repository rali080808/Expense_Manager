package com.example.task_1.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.task_1.R
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.ZoneId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

//@OptIn(ExperimentalTime::class)
//private fun Long.toDateString(): String {
//    val dt = Instant.fromEpochMilliseconds(this).toLocalDateTime(TimeZone.UTC)
//    return "%02d.%02d.%04d".format(dt.day, dt.month.number, dt.year)
//}

//TODO no strings
enum class PeriodFilter(val label: String) {
    TODAY("Today"),
    WEEK("This week"),
    MONTH("This month"),
    CUSTOM("Custom"),
    NONE("None")
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodFilterBar(
    onPeriodSelected: (periodFilter:PeriodFilter, startDate: String, endDate: String) -> Unit
) {
    var showDatePickerStart by remember { mutableStateOf(false) }
    var showDatePickerEnd by remember { mutableStateOf(false) }

    val options = PeriodFilter.entries
    var periodFilter by remember { mutableStateOf(PeriodFilter.MONTH) }
    var expandedPeriodFilter by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf(LocalDate.now()) }
    var endDate by remember { mutableStateOf(LocalDate.now()) }
    val datePickerState = rememberDatePickerState()

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = periodFilter.label,
            onValueChange = { },
            label = { Text(stringResource(R.string.date_period)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { expandedPeriodFilter = true })


        DropdownMenu(
            expanded = expandedPeriodFilter,
            onDismissRequest = { expandedPeriodFilter = false }) {
            PeriodFilter.entries.forEach { filter ->
                DropdownMenuItem(text = {
                    Text(filter.label)
                }, onClick = {
                    periodFilter = filter
                    expandedPeriodFilter = false
                })
            }
        }
    }
if ( periodFilter == PeriodFilter.CUSTOM) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = startDate.toString(),
            onValueChange = { },
            label = { Text(stringResource(R.string.start_date)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),

            )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDatePickerStart = true })
    }

    if (showDatePickerStart) {
        Row {
            DatePickerDialog(onDismissRequest = { showDatePickerStart = false }, confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        startDate =
                            java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                    }
                    showDatePickerStart = false
                }) { Text(stringResource(com.example.task_1.R.string.ok)) }
            }, dismissButton = {
                TextButton(onClick = {
                    showDatePickerStart = false
                }) { Text(stringResource(R.string.cancel)) }
            }) {
                DatePicker(state = datePickerState)
            }


        }
    }


    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = endDate.toString(),
            onValueChange = { },
            label = { Text(stringResource(R.string.end_date)) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),

            )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDatePickerEnd = true })
  if ( showDatePickerEnd )
        Row {
            DatePickerDialog(onDismissRequest = { showDatePickerEnd = false }, confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        endDate =
                            java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                    }
                    showDatePickerEnd = false
                }) { Text(stringResource(com.example.task_1.R.string.ok)) }
            }, dismissButton = {
                TextButton(onClick = {
                    showDatePickerEnd = false
                }) { Text(stringResource(R.string.cancel)) }
            }) {
                DatePicker(state = datePickerState)
            }

            DatePickerDialog(onDismissRequest = { showDatePickerEnd = false }, confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        endDate =
                            java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                                .toLocalDate()
                    }
                    showDatePickerEnd = false
                }) { Text(stringResource(com.example.task_1.R.string.ok)) }
            }, dismissButton = {
                TextButton(onClick = {
                    showDatePickerEnd = false
                }) { Text(stringResource(R.string.cancel)) }
            }) {
                DatePicker(state = datePickerState)
            }
        }
    }
}
    Button(
        onClick = { onPeriodSelected(periodFilter,
            startDate.toString(),
            endDate.toString()) }
    ) {
        Text(stringResource(R.string.filter))
    }
}
