package com.example.task_1.ui.dashboard

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.example.task_1.domain.Category
import com.example.task_1.domain.uiStates.DashboardUiState
import com.example.task_1.domain.ErrorCategory
import com.example.task_1.ui.ErrorDialog
import com.example.task_1.ui.dashboard.DashboardViewModel
import com.example.task_1.ui.LoadingScreen
import com.example.task_1.ui.TransactionCard

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel
) {
    val dashboardUiState by viewModel.uiState.collectAsState()
    val isRefreshing = dashboardUiState is DashboardUiState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing, onRefresh = { viewModel.loadData() }) {
        when (dashboardUiState) {
            DashboardUiState.Loading -> LoadingScreen()
            is DashboardUiState.Error -> ErrorDialog(
                (dashboardUiState as DashboardUiState.Error).message,
            //    (dashboardUiState as DashboardUiState.Error).args,
               loadData =  { viewModel.loadData() })

            is DashboardUiState.Success -> DashboardContent(

                transactions = (dashboardUiState as DashboardUiState.Success).transactions,
                totalExpenses = (dashboardUiState as DashboardUiState.Success).totalExpenses,
                biggestExpense = (dashboardUiState as DashboardUiState.Success).biggestExpense,
                categories = (dashboardUiState as DashboardUiState.Success).categories,
            )
        }

    }
}