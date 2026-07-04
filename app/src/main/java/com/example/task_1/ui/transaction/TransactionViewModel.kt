package com.example.task_1.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.data.DataService
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.DashboardUiState
import com.example.task_1.domain.NoFilter
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.Transactions
import com.example.task_1.domain.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SortTypes(val displayName: String) {
    SORTBY_DATE_ASCENDING("SORTBY_DATE_ASCENDING"),
    SORTBY_DATE_DESCENDING("SORTBY_DATE_DESCENDING"),
    SORTBY_SUM_ASCENDING("SORTBY_SUM_ASCENDING"),
    SORTBY_SUM_DESCENDING("SORTBY_SUM_DESCENDING"),
}


class TransactionViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState
    private val _allTransactions = MutableStateFlow(Transactions(mutableListOf()))
    val allTransactions: StateFlow<Transactions> = _allTransactions

    private val _filteredTransactions = MutableStateFlow(Transactions(mutableListOf()))
    val filteredTransactions: StateFlow<Transactions> = _filteredTransactions


    private val _categories = MutableStateFlow<Map<Int,Category>>(mapOf())
    val categories: StateFlow<Map<Int,Category>> = _categories

    var currentSortType = SortTypes.SORTBY_DATE_DESCENDING
     private set
    var currentCategoryFilter = NoFilter
    private set

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            _allTransactions.value = Transactions(
                dataService.getTransactionsObject()
                            .getTransactions()
                            .reversed()
                            .toMutableList()
            )
            _filteredTransactions.value = allTransactions.value
            _categories.value = dataService.getCategories()

            _uiState.value = UiState.Success(allTransactions.value)

        }
    }

    fun addTransaction(transaction: Transaction)  {
        viewModelScope.launch {


            if (transaction.categoryID == NoFilter) {
                _uiState.value = UiState.Error("Please, choose a category!")
                return@launch
            } else {
                _uiState.value = UiState.Loading
                dataService.addTransaction(transaction)
                filterByCategory(currentCategoryFilter)
                sortTransactions(currentSortType)

                _uiState.value = UiState.Success(allTransactions.value)
            }
        }
    }
    fun sortTransactions(sortType: SortTypes) {
      //  if (sortType == currentSortType) return;
        currentSortType = sortType;

        val currentList = filteredTransactions.value.getTransactions()

        val sortedList = when (sortType) {
            SortTypes.SORTBY_SUM_ASCENDING -> currentList.sortedBy { it.money }
            SortTypes.SORTBY_SUM_DESCENDING -> currentList.sortedByDescending { it.money }
            SortTypes.SORTBY_DATE_ASCENDING -> currentList.sortedBy { it.date }
            SortTypes.SORTBY_DATE_DESCENDING -> currentList.sortedByDescending { it.date }
        }.toMutableList()

        _filteredTransactions.value = Transactions(sortedList)

    }

    suspend fun filterByCategory(categoryID: Int) {
        //viewModelScope.launch {
            _uiState.value = UiState.Loading
            currentCategoryFilter = categoryID

            if (categoryID == NoFilter)
                _filteredTransactions.value =
                    Transactions(
                        allTransactions.value.getTransactions().toMutableList()
                    )
            else
                _filteredTransactions.value = Transactions(
                    allTransactions.value.getTransactions()
                        .filter { it.categoryID == categoryID }
                        .toMutableList()
                )
            sortTransactions(currentSortType)
            _uiState.value = UiState.Success(filteredTransactions.value)
        //}
    }

    fun getCategory(categoryID: Int): Category {
        return categories.value[categoryID] ?: run {
            _uiState.value = UiState.Error("Developer bug: Category ID not found")
            throw IllegalArgumentException("No category found with ID: $categoryID")
        }
    }
    fun getTransaction(transactionIndex: Int): Transaction {
        if (transactionIndex < 0 || transactionIndex >= filteredTransactions.value.getTransactions().size) {
            _uiState.value = UiState.Error("Developer bug: Transaction index out of bounds")
            throw IndexOutOfBoundsException("Transaction index $transactionIndex is out of bounds")
        }

        return filteredTransactions.value.getTransactions()[transactionIndex]
    }
}