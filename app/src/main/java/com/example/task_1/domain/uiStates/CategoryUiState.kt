package com.example.task_1.domain.uiStates

import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.category.CategoryFormField

sealed class CategoryUiState {
    object Loading : CategoryUiState()

    data class FormFilling(
        val transactions: List<Transaction>,
        val categories: List<Category>,
        val errors: Map<CategoryFormField, ErrorMessage>
    ) : CategoryUiState()

    data class Success(
        val transactions: List<Transaction>,
        val categories: List<Category>,

        ) : CategoryUiState()

    data class Error(val message: ErrorMessage) : CategoryUiState()
    // TODO developer bug state
}