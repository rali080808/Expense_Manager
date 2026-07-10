package com.example.task_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.task_1.data.IDataService
import com.example.task_1.data.RoomDataService
import com.example.task_1.data.local.AppDatabase
import com.example.task_1.ui.theme.Task_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database = AppDatabase.getDatabase(this)
        val categoryDao = database.categoryDao()
        val transactionDao = database.transactionDao()

         val roomDataService = RoomDataService(categoryDao, transactionDao)

        setContent {
            Task_1Theme(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                     Task_1App(dataService = roomDataService)
                }
            }
        }


        setContent {
            Task_1Theme(dynamicColor = false) {
                Task_1App(roomDataService)
            }
        }
    }
}

 @Composable
fun Task_1App(dataService: IDataService) {
    Task_1Theme(dynamicColor = false) {

        Navigation(dataService)
    }
}






