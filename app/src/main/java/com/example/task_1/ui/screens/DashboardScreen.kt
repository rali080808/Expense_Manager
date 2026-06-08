package com.example.task_1.ui.screens
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.items
@Composable
fun SummaryCard( text : String, money: Int) {

           Text("   $text: $money €   ", fontSize=16.sp, modifier = Modifier
               .padding(top=5.dp, start = 20.dp, end=20.dp)
               .border(width = 1.dp, color = Color.Blue)
               .background(color=Color.LightGray)
               .height(35.dp)

               .wrapContentHeight(align = Alignment.CenterVertically)
               , textAlign = TextAlign.Center
           )


}
@Composable
fun DashboardScreen(modifier : Modifier, fontSize: TextUnit, color: Color) {
    Column(Modifier.padding(start = 30.dp)) {
        Text("Dashboard", modifier = modifier, fontSize = fontSize, color = color)
        Text("Total", fontWeight = FontWeight.Bold, fontSize = 25.sp, fontStyle = FontStyle.Italic, modifier= Modifier.padding(top=5.dp))

        Row() {
            // TODO put data into a different file AND use an object for it
            SummaryCard( "Total sum", 30)
            SummaryCard( "Biggest expense", 8)
        }

        Text("Recent Transactions", fontWeight = FontWeight.Bold, fontSize = 25.sp, fontStyle = FontStyle.Italic, modifier= Modifier.padding(top=5.dp))
       // TODO class for hardcoded transactions
        val recentTransactions : List<String> = listOf<String>("a", "b", "c", "d", "e", "f")

        LazyColumn() {
            itemsIndexed(recentTransactions,
                key = {index, _ -> index}) { index,transaction ->
                Text(transaction)
            }
        }
        Text("Categories Overview", fontWeight = FontWeight.Bold, fontSize = 25.sp, fontStyle = FontStyle.Italic, modifier= Modifier.padding(top=5.dp))

    }
}