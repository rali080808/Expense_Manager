package com.example.task_1.domain.uiStates

import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.PeriodFilter
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.transaction.TransactionFormFields

sealed class TransactionUiState {
    object Loading : TransactionUiState()
    object Empty : TransactionUiState()
//    data class FormFilling(
//        val transactions: List<Transaction>,
//        val categories: List<Category>,
//        val errors: Map<TransactionFormFields, ErrorMessage>
//    ) : TransactionUiState()

    data class Success(
        val transactions: List<Transaction>,
        val categories: List<Category>,
        val errors: Map<TransactionFormFields, ErrorMessage>,
        val sent: Boolean,
        val periodFilter: PeriodFilter,
        val startDate: String,
        val endDate: String,
    ) : TransactionUiState()

    data class Error(
        val message: ErrorMessage
    ) : TransactionUiState()
}