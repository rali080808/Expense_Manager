package com.example.task_1.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.R
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.NoFilter
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.categoryExists
import com.example.task_1.domain.isChosenCategory
import com.example.task_1.domain.uiStates.TransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.onFailure

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
    private var errors = TransactionFormFields.entries.associateWith { ErrorMessage(R.string.empty_string) }.toMutableMap()
    var currentSortType = SortTypes.SORTBY_DATE_DESCENDING
        private set
    var currentCategoryFilter = NoFilter
        private set

    init {
        loadData()
    }
    fun putFormFillingState () {
        _uiState.value = TransactionUiState.FormFilling(filteredTransactions,
            categories, errors)
    }
    fun loadData() {
        viewModelScope.launch {
            _uiState.value = TransactionUiState.Loading

            allTransactions = dataService.getTransactions().reversed()

            filteredTransactions = allTransactions
            categories = dataService.getCategories()

            _uiState.value = TransactionUiState.Success(
                filteredTransactions, categories
            )

        }
    }

    fun showError(messageResId: Int, args: List<Any> = emptyList()) {
        _uiState.value = TransactionUiState.Error(ErrorMessage(
            messageID = messageResId, args = args
        ))
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            isChosenCategory(transaction.categoryID).onFailure { message ->
//                _uiState.value = TransactionUiState.Error(message)
                errors[TransactionFormFields.CATEGORY] = message
                _uiState.value = TransactionUiState.FormFilling(filteredTransactions,
                    categories, errors)
                return@launch // may be unnecessary
            }.onSuccess {
                _uiState.value = TransactionUiState.Loading
                dataService.addTransaction(transaction)
                allTransactions = dataService.getTransactions()
                filterByCategory(currentCategoryFilter)
                sortTransactions(currentSortType)

                _uiState.value = TransactionUiState.Success(
                    filteredTransactions, categories
                )
            }
        }
    }

    fun editTransaction(transaction: Transaction) {
        viewModelScope.launch {

            _uiState.value = TransactionUiState.Loading
            dataService.editTransaction(transaction)
            filterByCategory(currentCategoryFilter)
            sortTransactions(currentSortType)

            _uiState.value = TransactionUiState.Success(
                filteredTransactions, categories
            )

        }
    }

    fun sortTransactions(sortType: SortTypes) {
        _uiState.value = TransactionUiState.Loading

        currentSortType = sortType;

        val currentList = filteredTransactions

        val sortedList = when (sortType) {
            SortTypes.SORTBY_SUM_ASCENDING -> currentList.sortedBy { it.money }
            SortTypes.SORTBY_SUM_DESCENDING -> currentList.sortedByDescending { it.money }
            SortTypes.SORTBY_DATE_ASCENDING -> currentList.sortedBy { it.date }
            SortTypes.SORTBY_DATE_DESCENDING -> currentList.sortedByDescending { it.date }
        }

        filteredTransactions = sortedList

        _uiState.value = TransactionUiState.Success(filteredTransactions, categories)
    }

    fun filterByCategory(categoryID: Long) {
        _uiState.value = TransactionUiState.Loading
        currentCategoryFilter = categoryID

        if (categoryID == NoFilter)
            filteredTransactions = allTransactions
        else
            filteredTransactions = allTransactions.filter { it.categoryID == categoryID }


        sortTransactions(currentSortType)
        _uiState.value = TransactionUiState.Success(
            filteredTransactions, categories
        )

    }


}