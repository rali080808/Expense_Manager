package com.example.task_1.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.data.DataService
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Transactions
import com.example.task_1.domain.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState
    private val _transactions = MutableStateFlow(Transactions(mutableListOf()))
    val transactions: StateFlow<Transactions> = _transactions

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _transactions.value = Transactions(
                dataService .getTransactionsObject()
                            .getTransactions()
                            .reversed()
                            .toMutableList()
            )
            _uiState.value = UiState.Success(transactions.value)
        }
    }
}