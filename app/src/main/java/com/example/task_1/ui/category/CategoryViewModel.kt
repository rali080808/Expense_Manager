package com.example.task_1.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class CategoryViewModel(private val dataService: IDataService) : ViewModel() {
    var categoryIDForDeletion: Int = -1
    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> get() = _uiState

    private var transactions = Transactions(mutableListOf())
    private var categories = mutableMapOf<Int, Category>()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading

            transactions = Transactions(
                dataService.getTransactionsObject().getTransactions().reversed().toMutableList()
            )
            categories = dataService.getCategories().toMutableMap()

            _uiState.value = CategoryUiState.Success(transactions.getTransactions(), categories)

        }
    }

    fun transactionsInCategory(categoryID: Int): Boolean {  //TODO should be private
        for (transaction in transactions.getTransactions()) {
            if (transaction.categoryID == categoryID) {
                return true;
            }
        }
        return false;
    }

    fun removeCategory(categoryID: Int) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            if (transactionsInCategory(categoryID))
                _uiState.value =
                    CategoryUiState.Error("Category ${categories[categoryID]?.text} is still active and cannot be deleted. Edit it instead.")
            else {
                categories = dataService.removeCategory(categoryID).toMutableMap()
                _uiState.value = CategoryUiState.Success(transactions.getTransactions(), categories)
            }
        }
    }

    fun editCategory(categoryID: Int, editedCategory: Category) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            if (categories.containsKey(categoryID)) {
                categories = dataService.editCategory(categoryID, editedCategory).toMutableMap()
                _uiState.value = CategoryUiState.Success(transactions.getTransactions(), categories)
            } else {
                _uiState.value =
                    CategoryUiState.Error("Developer bug: categoryID $categoryID does not exist.")
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            val result =
                dataService.addCategory(Category(category.text, category.icon, category.color, 0.0))

            if (result.isSuccess) {
                categories = dataService.getCategories().toMutableMap()
                _uiState.value = CategoryUiState.Success(transactions.getTransactions(), categories)
            } else {
                _uiState.value = CategoryUiState.Error("Failed to add category")
            }
        }
    }

    fun validateIDForDeletion(categoryID: Int): Boolean {
        if (!categories.containsKey(categoryID)) {
            _uiState.value = CategoryUiState.Error("Developer bug. Invalid id"); return false
        } else if (transactionsInCategory(categoryID)) {
            _uiState.value =
                CategoryUiState.Error("Category ${categories[categoryID]?.text} ${categories[categoryID]?.icon} is active. You cannot delete it."); return false
        }
        return true
    }

    fun getCategory(categoryID: Int): Category {
        return categories[categoryID] ?: run {
            _uiState.value = CategoryUiState.Error("Developer bug: Category ID not found")
            throw IllegalArgumentException("No category found with ID: $categoryID")
        }
    }

    fun getTransaction(transactionIndex: Int): Transaction {
        if (transactionIndex < 0 || transactionIndex >= transactions.getTransactions().size) {
            _uiState.value = CategoryUiState.Error("Developer bug: Transaction index out of bounds")
            throw IndexOutOfBoundsException("Transaction index $transactionIndex is out of bounds")
        }

        return transactions.getTransactions()[transactionIndex]
    }
}