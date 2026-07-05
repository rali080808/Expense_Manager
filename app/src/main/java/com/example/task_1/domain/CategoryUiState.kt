package com.example.task_1.domain

sealed class CategoryUiState {
    object Loading : CategoryUiState()

    data class Success(
        val transactions: List<Transaction>,
        val categories: Map<Int, Category>,

    ) : CategoryUiState()

    data class Error(val message: String) : CategoryUiState()
    // TODO developer bug state
}