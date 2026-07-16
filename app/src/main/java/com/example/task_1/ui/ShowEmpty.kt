package com.example.task_1.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun ShowEmpty(message: Int) {
    Text(stringResource(message))
}