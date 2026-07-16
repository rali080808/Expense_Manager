package com.example.task_1.ui.dashboard

import android.icu.math.BigDecimal
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.R
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.PeriodFilter
import com.example.task_1.domain.uiStates.CategoryUiState
import com.example.task_1.domain.uiStates.DashboardUiState
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.uiStates.TransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> get() = _uiState
    private var transactions: List<Transaction> = listOf()
    private var categories: List<Category> = listOf()
    private val today = LocalDate.now()

    init { // TODO last 5
        loadData()
    }

    fun totalExpenses(): BigDecimal {
        var sum: BigDecimal = BigDecimal.ZERO;
        try {
            for (transaction in transactions) {
                sum = sum.add(BigDecimal(transaction.money))
            }
        } catch (e: Exception) {
            _uiState.value = DashboardUiState.Error(
                ErrorMessage(
                    R.string.error_please_try_again
                )
            )
        }
        return sum
    }

    fun getBiggestExpense(): String {
        if (transactions.isEmpty()) return "0";

        var indexOfTheBiggest: Int = 0
        var biggest: String = "0"
        try {
            for (transaction in transactions) {
                if (BigDecimal(transaction.money) > BigDecimal(transactions[indexOfTheBiggest].money)) {
                    indexOfTheBiggest = transactions.indexOf(transaction)
                }
            }
            biggest = transactions[indexOfTheBiggest].money
        } catch (e: Exception) {
            _uiState.value = DashboardUiState.Error(
                ErrorMessage(
                    R.string.error_please_try_again
                )
            )
        }
        return biggest
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading

            categories = dataService.getCategories()
            transactions = dataService.getTransactions().sortedByDescending { it.date }

            _uiState.value = DashboardUiState.Success(
                transactions = transactions,
                categories = categories,
                totalExpenses = totalExpenses(),
                biggestExpense = getBiggestExpense(),
                today = today.toString()
            )
        }
    }

    fun sortCategoriesByExpenseDesc(): List<Category> {
        return categories.sortedByDescending {
            it.expenseOnThisCategory(
                transactions = transactions,
                periodFilter =  PeriodFilter.MONTH,
                startDate = today.withDayOfMonth(1).toString(),
                endDate = today.toString()
            )
        }
    }


}