package com.example.task_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.task_1.ui.theme.Task_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Task_1Theme(dynamicColor = false) {
                Task_1App()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Task_1App() {
    Task_1Theme(dynamicColor = false) {
        Navigation()
    }
}






