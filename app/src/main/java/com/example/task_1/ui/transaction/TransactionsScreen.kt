package com.example.task_1.ui.transaction
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit

@Composable
fun TransactionsScreen(modifier : Modifier, style: TextStyle){
    Text("Transactions",
        modifier=modifier,
        style=style,
        color= MaterialTheme.colorScheme.primary)
}