package com.example.task_1.domain



sealed class UiState {
    object Loading : UiState()
    data class Success<out T>(val data: T) : UiState()
    data class Error(val message: String) : UiState()
}