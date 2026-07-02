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
    var categoryIDForDeletion: Int = -1
    var categoryIDForEdit: Int = -1
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState

    private val _categories = MutableStateFlow<Map<Int, Category>>(mapOf())
    val categories: StateFlow<Map<Int, Category>> = _categories
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

     fun transactionsInCategory(categoryID: Int): Boolean {  //TODO should be private
        for (transaction in transactions.value.getTransactions()) {
            if (transaction.categoryID == categoryID) {
                return true;
            }
        }
        return false;
    }

    fun removeCategory(categoryID: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            if (transactionsInCategory(categoryID))
                _uiState.value =
                    UiState.Error("Category ${categories.value[categoryID]?.text} is still active and cannot be deleted. Edit it instead.")
            else {
                _categories.value = dataService.removeCategory(categoryID)
                _uiState.value = UiState.Success(categories.value)
            }
        }
    }

    fun editCategory(categoryID: Int, editedCategory: Category) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            if (categories.value.containsKey(categoryID)) {
                _categories.value = dataService.editCategory(categoryID, editedCategory)
                _uiState.value = UiState.Success(categories.value)
            } else {
                _uiState.value = UiState.Error("Developer bug: categoryID $categoryID does not exist.")
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result =
                dataService.addCategory(Category(category.text, category.icon, category.color))

            if (result.isSuccess) {
                _categories.value = dataService.getCategories().toMap()
                _uiState.value = UiState.Success(categories.value)
            } else {
                _uiState.value = UiState.Error("Failed to add category")
            }
        }
    }
}