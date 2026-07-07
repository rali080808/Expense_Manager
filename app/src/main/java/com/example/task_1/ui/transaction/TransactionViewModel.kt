package com.example.task_1.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.R
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.NoFilter
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.TransactionUiState
import com.example.task_1.ui.TransactionInput
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SortTypes(val displayName: String) {
    SORTBY_DATE_ASCENDING("By date ascending"), SORTBY_DATE_DESCENDING("By date descending"), SORTBY_SUM_ASCENDING(
        "By sum ascending"
    ),
    SORTBY_SUM_DESCENDING("By sum descending"),
}


class TransactionViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> get() = _uiState
    private var allTransactions = listOf<Transaction>()
    private var filteredTransactions = listOf<Transaction>()
    private var categories = mapOf<Int, Category>()

    var currentSortType = SortTypes.SORTBY_DATE_DESCENDING
        private set
    var currentCategoryFilter = NoFilter
        private set

    init {
        loadData()
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
        _uiState.value = TransactionUiState.Error(
            message = messageResId, args = args
        )
    }

    fun addTransaction(transaction: TransactionInput) {
        viewModelScope.launch {
            if (transaction.categoryID == NoFilter) {
                _uiState.value = TransactionUiState.Error(R.string.please_choose_a_category)
                return@launch // may be unnecessary
            } else {
                _uiState.value = TransactionUiState.Loading
                dataService.addTransaction(transaction)
                filterByCategory(currentCategoryFilter)
                sortTransactions(currentSortType)

                _uiState.value = TransactionUiState.Success(
                    filteredTransactions, categories
                )
            }
        }
    }

    fun editTransaction(transactionID: String, transaction: TransactionInput) {
        viewModelScope.launch {

            _uiState.value = TransactionUiState.Loading
            dataService.editTransaction(transaction.toTransaction(transactionID))
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

    fun filterByCategory(categoryID: Int) {
        _uiState.value = TransactionUiState.Loading
        currentCategoryFilter = categoryID

        // if (categoryID == NoFilter) filteredTransactions = allTransactions
        //else
        filteredTransactions = allTransactions.filter { it.categoryID == categoryID }


        sortTransactions(currentSortType)
        _uiState.value = TransactionUiState.Success(
            filteredTransactions, categories
        )

    }

    fun getCategory(categoryID: Int): Category {
        return categories[categoryID] ?: run {
            _uiState.value = TransactionUiState.Error(
                R.string.developer_bug_categoryid_does_not_exist, args = listOf(categoryID)
            )
            throw IllegalArgumentException("No category found with ID: $categoryID")
        }
    }

    fun getTransaction(transactionId: String): Transaction {
        return filteredTransactions.find { it.id == transactionId }
            ?: run {
                _uiState.value = TransactionUiState.Error(
                    R.string.transaction_with_id_not_found,
                    args = listOf(transactionId)
                )
                throw IllegalArgumentException()
            }
    }
}