package com.example.task_1.ui.transaction

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.R
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.NoFilter
import com.example.task_1.domain.PeriodFilter
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.categoryExists
import com.example.task_1.domain.isChosenCategory
import com.example.task_1.domain.isPositive
import com.example.task_1.domain.uiStates.TransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.task_1.domain.Result
import com.example.task_1.domain.hasUpToTwoDecimalPlaces
import com.example.task_1.domain.isDateInRange
import com.example.task_1.domain.isNotEmpty
import com.example.task_1.domain.validateLength
import com.example.task_1.domain.validateMoney
import java.math.BigDecimal
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


enum class SortTypes(val displayName: String) {
    SORTBY_DATE_ASCENDING("By date ascending"), SORTBY_DATE_DESCENDING("By date descending"), SORTBY_SUM_ASCENDING(
        "By sum ascending"
    ),
    SORTBY_SUM_DESCENDING("By sum descending"),
}

enum class TransactionFormFields {
    SENDER, RECEIVER, MONEY, CATEGORY, DATE, PAY_METHOD, DESCRIPTION,
}

class TransactionViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> get() = _uiState
    private var allTransactions = listOf<Transaction>()
    private var filteredTransactions = listOf<Transaction>()
    private var categories = listOf<Category>()
    private var errors = mutableMapOf<TransactionFormFields, ErrorMessage>()
    var currentSortType = SortTypes.SORTBY_DATE_DESCENDING
    var currentCategoryFilter = NoFilter
    private var periodFilter = PeriodFilter.MONTH
    private val today: LocalDate = LocalDate.now()
    private var startDate: LocalDate = LocalDate.now()
    private var endDate: LocalDate = LocalDate.now()

    init {
        loadData()
    }

    fun putFormFillingState() {
        _uiState.value = TransactionUiState.Success(
            transactions = filteredTransactions,
            categories = categories,
            errors = errors,
            sent = false,
            periodFilter = periodFilter,
            startDate = startDate.toString(),
            endDate = endDate.toString()
        )
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = TransactionUiState.Loading

            allTransactions = dataService.getTransactions().reversed()

            filteredTransactions = allTransactions
            filterAndSort()
            categories = dataService.getCategories()

            _uiState.value = TransactionUiState.Success(
                transactions = filteredTransactions,
                categories = categories,
                errors = errors,
                sent = false,
                periodFilter = periodFilter,
                startDate = startDate.toString(),
                endDate = endDate.toString()
            )
        }
    }

    fun showError(messageResId: Int, args: List<Any> = emptyList()) {
        _uiState.value = TransactionUiState.Error(
            ErrorMessage(
                messageID = messageResId, args = args
            )
        )
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            errors = mutableMapOf()
            isChosenCategory(transaction.categoryID).onFailure { message ->
                errors[TransactionFormFields.CATEGORY] = message
            }

            var formattedMoney = transaction.money
            validateMoney(transaction.money).onFailure { message ->
                errors[TransactionFormFields.MONEY] = message
            }.onSuccess { moneyBigDecimal ->
                formattedMoney = moneyBigDecimal.stripTrailingZeros().toPlainString()
            }

            validateLength(
                transaction.sender, Transaction.MIN_NAME_LENGTH, Transaction.MAX_NAME_LENGTH
            ).onFailure { message ->
                errors[TransactionFormFields.SENDER] = message
            }

            validateLength(
                transaction.receiver, Transaction.MIN_NAME_LENGTH, Transaction.MAX_NAME_LENGTH
            ).onFailure { message ->
                errors[TransactionFormFields.RECEIVER] = message
            }

            if (errors.isNotEmpty()) {
                _uiState.value = TransactionUiState.Success(
                    transactions = filteredTransactions,
                    categories = categories,
                    errors = errors,
                    sent = false,
                    periodFilter = periodFilter,
                    startDate = startDate.toString(),
                    endDate = endDate.toString()
                )
                return@launch
            }
            _uiState.value = TransactionUiState.Loading
            dataService.addTransaction(transaction.copy(money = formattedMoney))
            allTransactions = dataService.getTransactions()
            filterAndSort()
            _uiState.value = TransactionUiState.Success(
                transactions = filteredTransactions,
                categories = categories,
                errors = errors,
                sent = true,
                periodFilter = periodFilter,
                startDate = startDate.toString(),
                endDate = endDate.toString()
            )

        }
    }

    fun deleteTransaction(transactionID: Long?) {
        viewModelScope.launch {
            if (transactionID == null) {
                _uiState.value =
                    TransactionUiState.Error(ErrorMessage(R.string.error_please_try_again))
            } else {
                allTransactions = dataService.deleteTransaction(transactionID)
                filterAndSort()
            }
        }
    }

    fun editTransaction(transaction: Transaction) {
        viewModelScope.launch {

            errors = mutableMapOf()
            isChosenCategory(transaction.categoryID).onFailure { message ->
                errors[TransactionFormFields.CATEGORY] = message
            }

            validateMoney(transaction.money).onFailure { message ->
                errors[TransactionFormFields.MONEY] = message
            }

            validateLength(
                transaction.sender, Transaction.MIN_NAME_LENGTH, Transaction.MAX_NAME_LENGTH
            ).onFailure { message ->
                errors[TransactionFormFields.SENDER] = message
            }

            validateLength(
                transaction.receiver, Transaction.MIN_NAME_LENGTH, Transaction.MAX_NAME_LENGTH
            ).onFailure { message ->
                errors[TransactionFormFields.RECEIVER] = message
            }

            if (errors.isNotEmpty()) {
                _uiState.value = TransactionUiState.Success(
                    transactions = filteredTransactions,
                    categories = categories,
                    errors = errors,
                    sent = false,
                    periodFilter = periodFilter,
                    startDate = startDate.toString(),
                    endDate = endDate.toString()
                )
                return@launch
            }

            _uiState.value = TransactionUiState.Loading
            dataService.editTransaction(transaction)
            allTransactions = dataService.getTransactions()
            filterAndSort()

            _uiState.value = TransactionUiState.Success(
                transactions = filteredTransactions,
                categories = categories,
                errors = errors,
                sent = true,
                periodFilter = periodFilter,
                startDate = startDate.toString(),
                endDate = endDate.toString()
            )

        }
    }

    fun sortTransactions(sortType: SortTypes) {
        _uiState.value = TransactionUiState.Loading

        currentSortType = sortType;

        val currentList = filteredTransactions

        val sortedList = when (sortType) {
            SortTypes.SORTBY_SUM_ASCENDING -> currentList.sortedBy { BigDecimal(it.money) }
            SortTypes.SORTBY_SUM_DESCENDING -> currentList.sortedByDescending { BigDecimal(it.money) }
            SortTypes.SORTBY_DATE_ASCENDING -> currentList.sortedBy { it.date }
            SortTypes.SORTBY_DATE_DESCENDING -> currentList.sortedByDescending { it.date }
        }

        filteredTransactions = sortedList

        _uiState.value = TransactionUiState.Success(
            transactions = filteredTransactions,
            categories = categories,
            errors = errors,
            sent = false,
            periodFilter = periodFilter,
            startDate = startDate.toString(),
            endDate = endDate.toString()
        )
    }

    fun filterByPeriod() {
        val list = mutableListOf<Transaction>()
        for (transaction in allTransactions) {
            if (LocalDate.parse(transaction.date).isDateInRange(
                    periodFilter = periodFilter,
                    startDate = LocalDate.parse(startDate.toString()),
                    endDate = LocalDate.parse(endDate.toString())
                )
            ) {
                list.add(transaction)
            }
        }
        filteredTransactions = list
    }

    fun setDateRange(
        periodFilter: PeriodFilter,
        startDate: String = today.toString(),
        endDate: String = today.toString()
    ) {
        _uiState.value = TransactionUiState.Loading
        this.periodFilter = periodFilter

        when (periodFilter) {
            PeriodFilter.MONTH -> {
                this.startDate = today.withDayOfMonth(1)
                this.endDate = today
            }

            PeriodFilter.WEEK -> {
                this.startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                Log.e("start of week", this.startDate.toString())
                this.endDate = today
                Log.e("end of week", this.endDate.toString())

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
        filterAndSort()

        _uiState.value = TransactionUiState.Success(
            transactions = filteredTransactions,
            categories = categories,
            errors = errors,
            sent = false,
            periodFilter = periodFilter,
            startDate = this.startDate.toString(),
            endDate = this.endDate.toString()
        )
    }

    fun filterAndSort() {
        filterByPeriod()
        filterByCategory(currentCategoryFilter)
        sortTransactions(currentSortType)
    }

    fun filterByCategory(categoryID: Long) {
        _uiState.value = TransactionUiState.Loading
        currentCategoryFilter = categoryID

        if (categoryID != NoFilter) filteredTransactions =
            filteredTransactions.filter { it.categoryID == categoryID }


        sortTransactions(currentSortType)
        _uiState.value = TransactionUiState.Success(
            transactions = filteredTransactions,
            categories = categories,
            errors = errors,
            sent = false,
            periodFilter = periodFilter,
            startDate = startDate.toString(),
            endDate = endDate.toString()
        )

    }

    fun groupByDate(): List<List<Transaction>> {
        if (filteredTransactions.isNotEmpty() && (currentSortType == SortTypes.SORTBY_DATE_ASCENDING || currentSortType == SortTypes.SORTBY_DATE_DESCENDING)) {
            val groupedTransactions = mutableListOf<List<Transaction>>()
            var list = mutableListOf<Transaction>()
            list.add(filteredTransactions[0])
            for (i in 1..<filteredTransactions.size) {
                if (filteredTransactions[i].date == filteredTransactions[i - 1].date) list.add(
                    filteredTransactions[i]
                )
                else {
                    groupedTransactions.add(list)
                    list = mutableListOf(filteredTransactions[i])
                }
            }
            groupedTransactions.add(list)
            return groupedTransactions
        }
        return listOf(filteredTransactions)
    }
}