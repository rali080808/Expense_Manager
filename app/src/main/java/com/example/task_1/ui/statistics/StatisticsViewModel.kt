package com.example.task_1.ui.statistics

import android.Manifest
import android.icu.math.BigDecimal
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.R
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.PeriodFilter
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.isDateInRange
import com.example.task_1.domain.uiStates.StatisticsUiState
import com.example.task_1.ui.PeriodFilterBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.toKotlinLocalIsoWeekDate
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.div

class StatisticsViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<StatisticsUiState>(StatisticsUiState.Loading)
    val uiState: StateFlow<StatisticsUiState> get() = _uiState
    private var transactions: List<Transaction> = listOf()
    private var categories: List<Category> = listOf()

    var periodFilter = PeriodFilter.MONTH
    val today: LocalDate = LocalDate.now()
    var startDate: LocalDate = LocalDate.now()
    var endDate: LocalDate = LocalDate.now()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = StatisticsUiState.Loading
            try {
                categories = dataService.getCategories()
                transactions = dataService.getTransactions().sortedByDescending { it.date }
                setDateRange(PeriodFilter.MONTH)
                _uiState.value = StatisticsUiState.Success(
                    totalCurrentMonth = totalExpenses(PeriodFilter.MONTH),
                    totalSelectedPeriod = totalExpenses(),
                    averageDaily = averageDaily(),
                    transactions = transactions,
                    periodFilter = periodFilter,
                    startDate = startDate.toString(),
                    endDate = endDate.toString(),
                    today = today.toString(),
                    selectedCurrency = transactions.getOrNull(0)?.currency?.sign ?: ""
                )
            } catch (e: Exception) {
                _uiState.value = StatisticsUiState.Error(
                    ErrorMessage(
                        R.string.error_please_try_again
                    )
                )
            }
        }
    }


    fun totalExpenses(periodFilter: PeriodFilter = this.periodFilter): BigDecimal {
        var sum: BigDecimal = BigDecimal.ZERO

        try {
            for (transaction in transactions) {
                if (LocalDate.parse(transaction.date).isDateInRange(
                        periodFilter = periodFilter, startDate = startDate, endDate = endDate
                    )
                ) {
                    sum = sum.add(BigDecimal(transaction.money))
                }
            }
        } catch (e: Exception) {
            _uiState.value = StatisticsUiState.Error(
                ErrorMessage(
                    R.string.error_please_try_again
                )
            )
        }
        return sum

    }

    fun sortCategoriesByExpenseDesc(): List<Category> {
        return categories.sortedByDescending {
            it.expenseOnThisCategory(
                transactions, periodFilter, startDate.toString(), endDate.toString()
            )
        }
    }

    fun averageDaily(): BigDecimal {
        var average: BigDecimal = BigDecimal.ZERO
        try {
            val days = ChronoUnit.DAYS.between(startDate, endDate) + 1
            average = if (days > 0) totalExpenses().divide(
                BigDecimal.valueOf(days), 2, BigDecimal.ROUND_HALF_UP
            ) else BigDecimal.ZERO

        } catch (e: Exception) {
            _uiState.value = StatisticsUiState.Error(
                ErrorMessage(
                    R.string.error_please_try_again
                )
            )
        }
        return average
    }

    fun setDateRange(
        periodFilter: PeriodFilter,
        startDate: String = today.toString(),
        endDate: String = today.toString()
    ) {
        _uiState.value = StatisticsUiState.Loading
        this.periodFilter = periodFilter
        try {
            when (periodFilter) {
                PeriodFilter.MONTH -> {
                    this.startDate = today.withDayOfMonth(1)
                    this.endDate = today
                }

                PeriodFilter.WEEK -> {
                    this.startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                    this.endDate = today
                }

                PeriodFilter.TODAY -> {
                    this.startDate = today
                    this.endDate = today
                }

                PeriodFilter.CUSTOM -> {
                    this.startDate = LocalDate.parse(startDate)
                    this.endDate = LocalDate.parse(endDate)
                }
            }

            _uiState.value = StatisticsUiState.Success(
                totalCurrentMonth = totalExpenses(PeriodFilter.MONTH),
                totalSelectedPeriod = totalExpenses(),
                averageDaily = averageDaily(),
                transactions = transactions,
                periodFilter = periodFilter,
                startDate = this.startDate.toString(),
                endDate = this.endDate.toString(),
                today = today.toString(),
                selectedCurrency = transactions.getOrNull(0)?.currency?.sign ?: ""
            )
        } catch (e: Exception) {
            _uiState.value = StatisticsUiState.Error(
                ErrorMessage(
                    R.string.error_please_try_again
                )
            )
        }
    }

}