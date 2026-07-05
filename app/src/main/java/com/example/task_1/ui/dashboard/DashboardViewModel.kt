package com.example.task_1.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.R
import com.example.task_1.data.DataService
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.CategoryUiState
import com.example.task_1.domain.DashboardUiState
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.Transactions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> get() = _uiState
    private var transactions: Transactions = Transactions(mutableListOf())
    private var categories: Map<Int, Category> = mapOf()
    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            categories = dataService.getCategories()
            transactions = Transactions(
                dataService .getTransactionsObject()
                            .getTransactions()
                            .reversed()
                            .toMutableList()
            )
            _uiState.value = DashboardUiState.Success(
                transactions=transactions.getTransactions(),
                categories=categories,
                totalExpenses=transactions.totalExpenses(),
                biggestExpense= transactions.getBiggestExpense(),
            )
        }
    }
    fun getCategory(categoryID: Int): Category {
        return categories[categoryID] ?: run {
            _uiState.value = DashboardUiState.Error(
                R.string.developer_bug_categoryid_does_not_exist, args = listOf(categoryID)
            )
            throw IllegalArgumentException("No category found with ID: $categoryID")
        }
    }

    fun getTransaction(transactionIndex: Int): Transaction {
        if (transactionIndex < 0 || transactionIndex >= transactions.getTransactions().size) {
            _uiState.value = DashboardUiState.Error(
                R.string.developer_bug_transaction_index_out_of_bounds,
                args = listOf(transactionIndex)
            )
            throw IndexOutOfBoundsException("Transaction index $transactionIndex is out of bounds")
        }

        return transactions.getTransactions()[transactionIndex]
    }


}