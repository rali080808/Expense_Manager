package com.example.task_1.ui.category

import com.example.task_1.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.data.IDataService
import com.example.task_1.domain.Category
import com.example.task_1.domain.ComponentMode
import com.example.task_1.domain.ErrorMessage
import com.example.task_1.domain.uiStates.CategoryUiState
import com.example.task_1.domain.uiStates.DashboardUiState
import com.example.task_1.domain.Transaction
import com.example.task_1.domain.categoryExists
import com.example.task_1.domain.containsEmojis
import com.example.task_1.domain.containsID
import com.example.task_1.domain.getById
import com.example.task_1.domain.isNotEmpty
import com.example.task_1.domain.validateIcon
import com.example.task_1.domain.validateLength
import com.example.task_1.domain.validateMoney
import com.example.task_1.ui.transaction.TransactionFormFields
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.set

enum class CategoryFormField {
    TEXT, ICON, COLOR
}

class CategoryViewModel(private val dataService: IDataService) : ViewModel() {
    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> get() = _uiState

    private var transactions = listOf<Transaction>()
    private var categories = listOf<Category>()

    private var errors = mutableMapOf<CategoryFormField, ErrorMessage>()


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

    private fun transactionsInCategory(categoryID: Long): Boolean {
        for (transaction in transactions) {
            if (transaction.categoryID == categoryID) {
                return true;
            }
        }
        return false;
    }

    fun removeCategory(categoryID: Long?) {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            if ( categoryID == null ) {
                _uiState.value = CategoryUiState.Error(ErrorMessage(R.string.error_please_try_again))
                return@launch
            }
            categories = dataService.removeCategory(categoryID)
            _uiState.value = CategoryUiState.Success(transactions, categories)

        }
    }

    fun editCategory(categoryID: Long, editedCategory: Category) {
        viewModelScope.launch {

            if (categories.containsID(categoryID)) {
              validateCategory(editedCategory, ComponentMode.EDIT)

                if (errors.isEmpty()) {
                    categories = dataService.editCategory(categoryID, editedCategory)
                    _uiState.value = CategoryUiState.Success(transactions, categories)
                }else {
                    _uiState.value = CategoryUiState.FormFilling(transactions, categories, errors)
                }
            } else {
                _uiState.value = CategoryUiState.Error(
                    ErrorMessage(
                        R.string.developer_bug_categoryid_does_not_exist, args = listOf(categoryID)
                    )
                )
            }
        }
    }

    fun validateCategory(category: Category, componentMode: ComponentMode){
        errors = mutableMapOf()
        validateLength(
            category.text,
            Category.MIN_TEXT_LENGTH,
            Category.MAX_TEXT_LENGTH
        ).onFailure { message ->
            errors[CategoryFormField.TEXT] = message
        }
        containsEmojis(category.text).onFailure {  message ->
            errors[CategoryFormField.TEXT] = message
        }
        val categoryID = if (componentMode == ComponentMode.ADD) null else category.id
        categoryExists(category.text, categories, categoryID).onFailure { message ->
            errors[CategoryFormField.TEXT] = message
        }

        validateIcon(
            category.icon
        ).onFailure { message ->
            errors[CategoryFormField.ICON] = message
        }
    }
    fun addCategory(category: Category) {
        viewModelScope.launch {
            errors = mutableMapOf()
            _uiState.value = CategoryUiState.FormFilling(transactions, categories, errors)

            validateCategory(category, ComponentMode.ADD)

            if (errors.isEmpty()) {
                dataService.addCategory(Category(null, category.text, category.icon, category.color))
                categories = dataService.getCategories()
                _uiState.value = CategoryUiState.Success(transactions, categories)
            }else {
                _uiState.value = CategoryUiState.FormFilling(transactions, categories, errors)
            }

        }
    }

    // TODO check whether to remove Boolean from return type
    fun validateIDForDeletion(categoryID: Long?): Boolean {


        if (categoryID == null) {
            _uiState.value =
                CategoryUiState.Error(ErrorMessage(R.string.please_try_again, args = listOf()))
            return false
        }
        if (categories.containsID(categoryID)) {
            if (transactionsInCategory(categoryID)) {
              return false
            }
            return true
        }
        throw IllegalArgumentException("Category ID $categoryID does not exist.")

    }


}

