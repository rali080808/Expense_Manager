package com.example.task_1.ui.statistics

import android.Manifest
import android.icu.math.BigDecimal
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
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

            categories = dataService.getCategories()
            transactions = dataService.getTransactions().sortedByDescending { it.date }
            setDateRange(PeriodFilter.MONTH)
            _uiState.value = StatisticsUiState.Success(
                totalExpenses(PeriodFilter.MONTH),
                totalExpenses(),
                averageDaily(),
                transactions,
                periodFilter,
                startDate.toString(),
                endDate.toString(),
                today.toString(),
                transactions.getOrNull(0)?.currency?.sign ?: ""
            )
        }
    }


    fun totalExpenses(periodFilter: PeriodFilter = this.periodFilter): BigDecimal {
        var sum: BigDecimal = BigDecimal.ZERO

        for (transaction in transactions) {
            if (LocalDate.parse(transaction.date).isDateInRange(
                    periodFilter = periodFilter,
                    startDate = startDate,
                    endDate = endDate
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
                startDate.toString(),
                endDate.toString()
            )
        }
    }

    fun averageDaily(): BigDecimal {
        val days = ChronoUnit.DAYS.between(startDate, endDate) + 1

        return if (days > 0) totalExpenses().divide(
            BigDecimal.valueOf(days),
            2,
            BigDecimal.ROUND_HALF_UP
        ) else BigDecimal.ZERO
    }

    fun setDateRange(periodFilter: PeriodFilter, startDate: String = today.toString(), endDate: String = today.toString()) {
        _uiState.value = StatisticsUiState.Loading
        this.periodFilter = periodFilter

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

    }

}