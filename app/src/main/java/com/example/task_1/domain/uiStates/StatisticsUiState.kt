package com.example.task_1.domain.uiStates

import android.icu.math.BigDecimal
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.PeriodFilter
import com.example.task_1.domain.Transaction
 import kotlinx.coroutines.CoroutineStart
import java.time.Month

sealed class StatisticsUiState {
    object Loading : StatisticsUiState()

    data class Success(
        val totalCurrentMonth: BigDecimal,
        val totalSelectedPeriod: BigDecimal,
        val averageDaily: BigDecimal,
        val transactions: List<Transaction>,
        val periodFilter: PeriodFilter,
        val startDate: String,
        val endDate: String,
        val today: String,
        val selectedCurrency: String
    ) : StatisticsUiState()

    data class Error(val message: ErrorMessage) : StatisticsUiState()
}