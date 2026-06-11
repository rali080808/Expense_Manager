package com.example.task_1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryCard( text : String, money: Double) {

    Text("   $text: $money €   ", fontSize=16.sp, modifier = Modifier
        .padding(top=5.dp, start = 15.dp, end=15.dp)
        .border(width = 1.dp, color = Color.Blue)
        .background(color=Color.LightGray)
        .height(35.dp)
        .wrapContentHeight(align = Alignment.CenterVertically)
        , textAlign = TextAlign.Center
    )
}