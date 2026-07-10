package com.example.task_1.ui.category

import com.example.task_1.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.data.DataService
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.uiStates.CategoryUiState
import com.example.task_1.domain.uiStates.DashboardUiState
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.containsID
import com.example.task_1.domain.getById
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> get() = _uiState

    private var transactions = listOf<Transaction>()
    private var categories = listOf<Category>()

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

    fun transactionsInCategory(categoryID: Long): Boolean {  //TODO should be private
        for (transaction in transactions) {
            if (transaction.categoryID == categoryID) {
                return true;
            }
        }
        return false;
    }

    fun removeCategory(categoryID: Long) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading

            categories = dataService.removeCategory(categoryID)
            _uiState.value = CategoryUiState.Success(transactions, categories)

        }
    }

    fun editCategory(categoryID: Long, editedCategory: Category) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            if (categories.containsID(categoryID)) {
                categories = dataService.editCategory(categoryID, editedCategory)
                _uiState.value = CategoryUiState.Success(transactions, categories)
            } else {
                _uiState.value = CategoryUiState.Error(
                    ErrorMessage(
                        R.string.developer_bug_categoryid_does_not_exist, args = listOf(categoryID)
                    ))
            }
        }
    }


    fun addCategory(category: Category) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            if (categories.containsText(category.text)) {
                _uiState.value = CategoryUiState.Error( ErrorMessage(
                    R.string.category_with_a_name_already_exist_you_cannot_add_it_again_but_you_can_edit_the_old_one,
                    args = listOf(category.text)
                ))
                return@launch
            }
            dataService.addCategory(Category(null, category.text, category.icon, category.color))
            categories = dataService.getCategories()
            _uiState.value = CategoryUiState.Success(transactions, categories)

        }
    }

    // TODO check whether to remove Boolean from return type
    fun validateIDForDeletion(categoryID: Long?): Boolean {
        if (categoryID == null) {
            _uiState.value = CategoryUiState.Error( ErrorMessage(R.string.please_try_again, args = listOf()))
            return false
        }
        if (categories.containsID(categoryID)) {
            if (transactionsInCategory(categoryID)) {
                _uiState.value = CategoryUiState.Error(
                    ErrorMessage(R.string.category_is_active_you_cannot_delete_it, args = listOf(
                        categories.getById(categoryID)?.text ?: "",
                        categories.getById(categoryID)?.icon ?: ""
                    ))
                ); return false
            }
            return true
        }
        throw IllegalArgumentException("Category ID $categoryID does not exist.")

    }


}

fun List<Category>.containsText(text: String): Boolean {
    for (category in this) {
        if (category.text == text) return true;
    }
    return false;
}