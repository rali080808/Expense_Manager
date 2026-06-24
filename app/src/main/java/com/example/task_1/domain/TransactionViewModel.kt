package com.example.task_1.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.example.task_1.data.DataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SortTypes(val displayName: String) {
    SORTBY_DATE_ASCENDING("SORTBY_DATE_ASCENDING"),
    SORTBY_DATE_DESCENDING("SORTBY_DATE_DESCENDING"),
    SORTBY_SUM_ASCENDING("SORTBY_SUM_ASCENDING"),
    SORTBY_SUM_DESCENDING("SORTBY_SUM_DESCENDING"),
}


class TransactionViewModel(private val dataService: DataService) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState
    private val _transactions = MutableStateFlow(Transactions(mutableListOf()))
    val transactions: StateFlow<Transactions> = _transactions

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories: StateFlow<List<Category>> = _categories

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

            _transactions.value = Transactions(
                dataService.getTransactionsObject().getTransactions().reversed().toMutableList()
            )
            _categories.value = dataService.getCategories()

            _uiState.value = UiState.Success(transactions.value)

        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            dataService.addTransaction(transaction)
            filterByCategory(currentCategoryFilter)
            sortTransactions(currentSortType)

            _uiState.value = UiState.Success(transactions.value)
        }
    }



    fun sortTransactions(sortType: SortTypes) {
      //  if (sortType == currentSortType) return;
        currentSortType = sortType;

        val currentList = _transactions.value.getTransactions()

        val sortedList = when (sortType) {
            SortTypes.SORTBY_SUM_ASCENDING -> currentList.sortedBy { it.money }
            SortTypes.SORTBY_SUM_DESCENDING -> currentList.sortedByDescending { it.money }
            SortTypes.SORTBY_DATE_ASCENDING -> currentList.sortedBy { it.date }
            SortTypes.SORTBY_DATE_DESCENDING -> currentList.sortedByDescending { it.date }
        }.toMutableList()

        _transactions.value = Transactions(sortedList)

    }

    suspend fun filterByCategory(category: Category) {
        //viewModelScope.launch {
            _uiState.value = UiState.Loading
            currentCategoryFilter = category

            if (category == NoFilter)
                _transactions.value =
                    Transactions(dataService.getTransactionsObject().getTransactions().toMutableList())
            else
                _transactions.value = Transactions(
                    dataService.getTransactionsObject().getTransactions()
                        .filter { it.category == category }.toMutableList()
                )
            sortTransactions(currentSortType)
            _uiState.value = UiState.Success(transactions.value)
        //}
    }
}