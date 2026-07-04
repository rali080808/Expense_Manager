package com.example.task_1.domain

sealed class DashboardUiState {
    object Loading : DashboardUiState()

    data class Success(
        val transactions: List<Transaction>,
        val categories: Map<Int, Category>,
        val totalExpenses: Double,
        val biggestExpense: Double,
    ) : DashboardUiState()

    data class Error(val message: String) : DashboardUiState()
}