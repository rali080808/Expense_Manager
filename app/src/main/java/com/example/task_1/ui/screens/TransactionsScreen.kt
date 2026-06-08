package com.example.task_1.ui.screens
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit

@Composable
fun TransactionsScreen(modifier : Modifier, fontSize: TextUnit, color:Color){
    Text("Transactions", modifier=modifier, fontSize=fontSize, color=color)
}