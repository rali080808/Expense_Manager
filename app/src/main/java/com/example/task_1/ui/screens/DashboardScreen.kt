package com.example.task_1.ui.screens
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.task_1.data.transactions
import com.example.task_1.domain.CategoriesOverview
import com.example.task_1.domain.Category
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.Transactions
import com.example.task_1.ui.CategoryOverviewCard
import com.example.task_1.ui.SummaryCard
import com.example.task_1.ui.TransactionCard
import java.time.LocalDate



@Composable
fun DashboardScreen(modifier : Modifier, fontSize: TextUnit, color: Color) {
    Column(Modifier.padding(start = 10.dp)) {
        Text("Dashboard", modifier = modifier, fontSize = fontSize, color = color)
        Text("Total", fontWeight = FontWeight.Bold, fontSize = 25.sp, fontStyle = FontStyle.Italic, modifier= Modifier.padding(top=5.dp))

        Row() {
            // TODO put data into a different file AND use an object for it
            SummaryCard("Total sum", transactions.totalExpenses())
            SummaryCard("Biggest expense", transactions.transactions[transactions.indexOfBiggestExpense()].money)
        }

        Text("Recent Transactions", fontWeight = FontWeight.Bold, fontSize = 25.sp, fontStyle = FontStyle.Italic, modifier= Modifier.padding(top=5.dp))

        LazyColumn(modifier= Modifier.height(500.dp)) {
            itemsIndexed(transactions.transactions,
                key = {index, _ -> index}) { index,transaction ->
                TransactionCard(transaction)
            }
        }
        Text("Categories Overview", fontWeight = FontWeight.Bold, fontSize = 25.sp, fontStyle = FontStyle.Italic, modifier= Modifier.padding(top=5.dp))
        val categoryExpenses: List<CategoriesOverview> = transactions.categoriesOverview();
        Column {
            categoryExpenses.forEach { item ->
                CategoryOverviewCard(item, transactions)
            }
        }
    }
}