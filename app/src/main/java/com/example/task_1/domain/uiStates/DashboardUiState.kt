package com.example.task_1.domain.uiStates

import android.icu.math.BigDecimal
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.Transaction

sealed class DashboardUiState {
    object Loading : DashboardUiState()

    data class Success(
        val transactions: List<Transaction>,
        val categories: List<Category>,
        val totalExpenses: BigDecimal,
        val biggestExpense: String,
        val today: String
    ) : DashboardUiState()

    data class Error(
        val message: ErrorMessage
    ) : DashboardUiState()
}