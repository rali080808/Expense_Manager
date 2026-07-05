package com.example.task_1.domain

sealed class TransactionUiState {
    object Loading : TransactionUiState()
    //TODO empty state

    data class Success(
        val transactions: List<Transaction>,
        val categories: Map<Int, Category>,
    ) : TransactionUiState()

    data class Error(
        val message: Int,
        val args: List<Any> = emptyList()
    ) : TransactionUiState()
}