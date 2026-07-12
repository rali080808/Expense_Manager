package com.example.task_1.ui.dashboard

import android.icu.math.BigDecimal
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.R
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.uiStates.CategoryUiState
import com.example.task_1.domain.uiStates.DashboardUiState
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.uiStates.TransactionUiState
import com.example.task_1.ui.PeriodFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class DashboardViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> get() = _uiState
    private var transactions: List<Transaction> = listOf()
    private var categories: List<Category> = listOf()
    private val today = LocalDate.now().toString()
    init {
        loadData()
    }

    fun totalExpenses(): BigDecimal {
        var sum: BigDecimal = BigDecimal.ZERO;
        for (transaction in transactions) {
            sum = sum.add(BigDecimal(transaction.money))
        }
        return sum
    }

    fun getBiggestExpense(): String {
        if (transactions.isEmpty()) return "0";

        var indexOfTheBiggest: Int = 0;
        for (transaction in transactions) {
            if (BigDecimal(transaction.money) > BigDecimal(transactions[indexOfTheBiggest].money)) {
                indexOfTheBiggest = transactions.indexOf(transaction)
            }
        }
        return transactions[indexOfTheBiggest].money;
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
                today = today
            )
        }
    }

    fun sortCategoriesByExpenseDesc(): List<Category> {
        return categories.sortedByDescending { it.expenseOnThisCategory(transactions, PeriodFilter.NONE, today, today) }
    }


}