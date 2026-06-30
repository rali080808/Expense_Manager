package com.example.task_1.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.data.DataService
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.Transactions
import com.example.task_1.domain.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val dataService: IDataService) : ViewModel() {
    var indexForDeletion: Int = -1
    var indexForEdit: Int = -1
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories: StateFlow<List<Category>> = _categories
    private val _transactions = MutableStateFlow(Transactions(mutableListOf()))
    val transactions: StateFlow<Transactions> = _transactions

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

    fun transactionsInCategory() : Boolean {
        for ( transaction in transactions.value.getTransactions()) {
            if ( transaction.category == categories.value[indexForDeletion]) {
                return true;
            }
        }
        return false;
    }

    fun removeCategory(index: Int){
        viewModelScope.launch {
        _uiState.value = UiState.Loading
        _categories.value = dataService.removeCategory(index)
        _uiState.value = UiState.Success(categories.value)
    }}

    fun editCategory(index: Int, editedCategory: Category) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _categories.value = dataService.editCategory(index, editedCategory)
            _uiState.value = UiState.Success(categories.value)
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = dataService.addCategory(Category(category.text, category.icon, category.color))

            if (result.isSuccess) {
                _categories.value = dataService.getCategories()
                _uiState.value = UiState.Success(categories.value)
            }else {
                _uiState.value = UiState.Error("Failed to add category")
            }
        }
    }
}