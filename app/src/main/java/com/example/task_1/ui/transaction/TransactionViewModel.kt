package com.example.task_1.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.R
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.NoFilter
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.TransactionUiState
import com.example.task_1.domain.Transactions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SortTypes(val displayName: String) {
    SORTBY_DATE_ASCENDING("By date ascending"),
    SORTBY_DATE_DESCENDING("By date descending"),
    SORTBY_SUM_ASCENDING("By sum ascending"),
    SORTBY_SUM_DESCENDING("By sum descending"),
}


class TransactionViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> get() = _uiState
    private var allTransactions = Transactions(mutableListOf())
    private var filteredTransactions = Transactions(mutableListOf())
    private var categories = mutableMapOf<Int, Category>()

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

            allTransactions = Transactions(
                dataService.getTransactionsObject()
                    .getTransactions()
                    .reversed()
                    .toMutableList()
            )
            filteredTransactions = allTransactions
            categories = dataService.getCategories().toMutableMap()

            _uiState.value = TransactionUiState.Success(
                filteredTransactions.getTransactions(),
                categories
            )

        }
    }

    fun showError(messageResId: Int, args: List<Any> = emptyList()) {
        _uiState.value = TransactionUiState.Error(
            message = messageResId,
            args = args
        )
    }

    fun addTransaction(transaction: Transaction) {
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
                    filteredTransactions.getTransactions(),
                    categories
                )
            }
        }
    }

    fun sortTransactions(sortType: SortTypes) {
        _uiState.value = TransactionUiState.Loading

        currentSortType = sortType;

        val currentList = filteredTransactions.getTransactions()

        val sortedList = when (sortType) {
            SortTypes.SORTBY_SUM_ASCENDING -> currentList.sortedBy { it.money }
            SortTypes.SORTBY_SUM_DESCENDING -> currentList.sortedByDescending { it.money }
            SortTypes.SORTBY_DATE_ASCENDING -> currentList.sortedBy { it.date }
            SortTypes.SORTBY_DATE_DESCENDING -> currentList.sortedByDescending { it.date }
        }.toMutableList()

        filteredTransactions = Transactions(sortedList)

        _uiState.value =
            TransactionUiState.Success(filteredTransactions.getTransactions(), categories)
    }

    fun filterByCategory(categoryID: Int) {
        _uiState.value = TransactionUiState.Loading
        currentCategoryFilter = categoryID

        if (categoryID == NoFilter)
            filteredTransactions =
                Transactions(
                    allTransactions.getTransactions().toMutableList()
                )
        else
            filteredTransactions = Transactions(
                allTransactions.getTransactions()
                    .filter { it.categoryID == categoryID }
                    .toMutableList()
            )
        sortTransactions(currentSortType)
        _uiState.value = TransactionUiState.Success(
            filteredTransactions.getTransactions(),
            categories
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

    fun getTransaction(transactionIndex: Int): Transaction {
        if (transactionIndex < 0 || transactionIndex >= filteredTransactions.getTransactions().size) {
            _uiState.value = TransactionUiState.Error(
                R.string.developer_bug_transaction_index_out_of_bounds,
                args = listOf(transactionIndex)
            )
            throw IndexOutOfBoundsException("Transaction index $transactionIndex is out of bounds")
        }

        return filteredTransactions.getTransactions()[transactionIndex]
    }
}