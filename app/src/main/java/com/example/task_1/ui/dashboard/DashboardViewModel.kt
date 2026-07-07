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
import com.example.task_1.domain.TransactionUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> get() = _uiState
    private var transactions: List<Transaction> = listOf()
    private var categories: Map<Int, Category> = mapOf()

    init {
        loadData()
    }

    fun totalExpenses(): Double {
        var sum: Double = 0.0;
        for (transaction in transactions) {
            sum += transaction.money;
        }
        return sum;
    }

    fun getBiggestExpense(): Double {
        if (transactions.isEmpty()) return 0.0;

        var indexOfTheBiggest: Int = 0;
        for (transaction in transactions) {
            if (transaction.money > transactions[indexOfTheBiggest].money) {
                indexOfTheBiggest = transactions.indexOf(transaction)
            }
        }
        return transactions[indexOfTheBiggest].money;
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            categories = dataService.getCategories()
            transactions = dataService.getTransactions()


            _uiState.value = DashboardUiState.Success(
                transactions = transactions,
                categories = categories,
                totalExpenses = totalExpenses(),
                biggestExpense = getBiggestExpense(),
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

    fun getTransaction(transactionId: String): Transaction {
        return transactions.find { it.id == transactionId } ?: run {
            _uiState.value = DashboardUiState.Error(
                R.string.transaction_with_id_not_found, args = listOf(transactionId)
            )
            throw IllegalArgumentException()
        }
    }


}