package com.example.task_1.ui.statistics

import android.Manifest
import android.icu.math.BigDecimal
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.isDateInRange
import com.example.task_1.domain.uiStates.StatisticsUiState
import com.example.task_1.ui.PeriodFilter
import com.example.task_1.ui.PeriodFilterBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalIsoWeekDate
import java.time.LocalDate

class StatisticsViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState> get() = _uiState
    private var transactions: List<Transaction> = listOf()
    private var categories: List<Category> = listOf()

    var periodFilter = PeriodFilter.MONTH
    val today = LocalDate.now().toString()
    var startDate = LocalDate.now().toString()
    var endDate = LocalDate.now().toString()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = StatisticsUiState.Loading

            categories = dataService.getCategories()
            transactions = dataService.getTransactions().sortedByDescending { it.date }

            _uiState.value = StatisticsUiState.Success(
                totalExpenses(PeriodFilter.MONTH),
                totalExpenses(),
                averageDaily(),
                 transactions,
                periodFilter,
                startDate,
                endDate,
                today
            )
        }
    }


    fun totalExpenses(periodFilter: PeriodFilter = this.periodFilter): BigDecimal {
        var sum: BigDecimal = BigDecimal.ZERO

        for (transaction in transactions) {
            if (LocalDate.parse(transaction.date).isDateInRange(
                    periodFilter = periodFilter,
                    startDate = if (periodFilter == PeriodFilter.MONTH) LocalDate.parse(today)
                    else LocalDate.parse(startDate),
                    endDate = LocalDate.parse(endDate)
                )
            ) {
                sum = sum.add(BigDecimal(transaction.money))
            }
        }
        return sum
    }


    fun sortCategoriesByExpenseDesc(): List<Category> {
        return categories.sortedByDescending {
            it.expenseOnThisCategory(
                transactions,
                periodFilter,
                startDate,
                endDate
            )
        }
    }

    fun averageDaily(): BigDecimal {
        return BigDecimal.ZERO
        // TODO
    }

    fun setDateRange(periodFilter: PeriodFilter, startDate: String, endDate: String) {
        _uiState.value = StatisticsUiState.Loading
        this.periodFilter = periodFilter
        if (periodFilter != PeriodFilter.CUSTOM) {
            this.startDate = today
            this.endDate = today
        }
        this.startDate = startDate
        this.endDate = endDate
        _uiState.value = StatisticsUiState.Success(
            totalExpenses(PeriodFilter.MONTH),
            totalExpenses(),
            averageDaily(),
             transactions,
            periodFilter,
            startDate,
            endDate,
            today
        )

    }

}