package com.example.task_1.ui.category

import com.example.task_1.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.data.DataService
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.CategoryUiState
import com.example.task_1.domain.DashboardUiState
import com.example.task_1.domain.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> get() = _uiState

    private var transactions = listOf<Transaction>()
    private var categories = mapOf<Int, Category>()

    init {
        loadData()
    }


    fun loadData() {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading

            transactions = dataService.getTransactions()

            categories = dataService.getCategories()

            _uiState.value = CategoryUiState.Success(transactions, categories)

        }
    }

    fun transactionsInCategory(categoryID: Int): Boolean {  //TODO should be private
        for (transaction in transactions) {
            if (transaction.categoryID == categoryID) {
                return true;
            }
        }
        return false;
    }

    fun removeCategory(categoryID: Int) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading

            categories = dataService.removeCategory(categoryID)
            _uiState.value = CategoryUiState.Success(transactions, categories)

        }
    }

    fun editCategory(categoryID: Int, editedCategory: Category) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            if (categories.containsKey(categoryID)) {
                categories = dataService.editCategory(categoryID, editedCategory)
                _uiState.value = CategoryUiState.Success(transactions, categories)
            } else {
                _uiState.value = CategoryUiState.Error(
                    R.string.developer_bug_categoryid_does_not_exist, args = listOf(categoryID)
                )
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            if (categories.containsText(category.text)) {
                _uiState.value = CategoryUiState.Error(
                    R.string.category_with_a_name_already_exist_you_cannot_add_it_again_but_you_can_edit_the_old_one,
                    args = listOf(category.text)
                )
                return@launch
            }
            dataService.addCategory(Category(category.text, category.icon, category.color, 0.0))
            categories = dataService.getCategories()
            _uiState.value = CategoryUiState.Success(transactions, categories)

        }
    }

    // TODO check whether to remove Boolean from return type
    fun validateIDForDeletion(categoryID: Int): Boolean {
        if (categories.containsKey(categoryID)) {
            if (transactionsInCategory(categoryID)) {
                _uiState.value = CategoryUiState.Error(
                    R.string.category_is_active_you_cannot_delete_it, args = listOf(
                        categories[categoryID]?.text ?: "", categories[categoryID]?.icon ?: ""
                    )
                ); return false
            }

        }
        throw IllegalArgumentException("Category ID $categoryID does not exist.")

        return true
    }

    fun getCategory(categoryID: Int): Category {
        return categories[categoryID]
            ?: throw IllegalArgumentException("Category ID $categoryID does not exist.")
    }


    fun getTransaction(transactionId: String): Transaction {
        return transactions.find { it.id == transactionId } ?: run {
            _uiState.value = CategoryUiState.Error(
                R.string.transaction_with_id_not_found, args = listOf(transactionId)
            )
            throw IllegalArgumentException()
        }
    }
}

fun Map<Int, Category>.containsText(text: String): Boolean {
    for ((id, category) in this) {
        if (category.text == text) return true;
    }
    return false;
}