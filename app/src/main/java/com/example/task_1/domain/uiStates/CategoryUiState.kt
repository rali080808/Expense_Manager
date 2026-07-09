package com.example.task_1.domain.uiStates

import com.example.task_1.domain.Category
import com.example.task_1.domain.Transaction

sealed class CategoryUiState {
    object Loading : CategoryUiState()

    data class Success(
        val transactions: List<Transaction>,
        val categories: List<Category>,

        ) : CategoryUiState()

    data class Error(val message: Int,
                     val args: List<Any> = emptyList()
    ) : CategoryUiState()
    // TODO developer bug state
}