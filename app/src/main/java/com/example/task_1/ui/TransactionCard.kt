package com.example.task_1.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.theme.Money

@Composable
fun TransactionCard(transaction: Transaction){
    Column(modifier = Modifier.border(width = 3.dp, color=Color.Blue).padding(bottom=20.dp).width(300.dp)){
        Row {
            Text(transaction.sender + " -> " + transaction.receiver, fontSize = 20.sp, modifier = Modifier.padding(10.dp))
            Text("" + transaction.money + " " + transaction.currency, fontSize=20.sp, modifier = Modifier.padding(start=20.dp, end=10.dp, top=10.dp) , color= Money)
        }
        Text(transaction.sender + " gave " + transaction.money + " " +  transaction.currency + " to " + transaction.receiver + " on " + transaction.date.toString() + ".", fontSize=16.sp, modifier = Modifier.padding(10.dp))
    }
}