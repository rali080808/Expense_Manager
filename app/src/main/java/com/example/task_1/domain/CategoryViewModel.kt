package com.example.task_1.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_1.data.DataService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel(private val dataService: DataService) : ViewModel() {
    var indexForDeletion: Int = -1
    var indexForEdit: Int = -1
    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState>  get() = _uiState

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories: StateFlow<List<Category>> = _categories



    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            _categories.value = dataService.getCategories()
            _uiState.value = UiState.Success(categories.value)
        }
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

    fun addCategory(text: String, icon: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result  = DataService.addCategory(text, icon)

            if (result.isSuccess) {
                _categories.value = DataService.getCategories()
                _uiState.value = UiState.Success(categories.value)
            }else {
                _uiState.value = UiState.Error("Failed to add category")
                _uiState.value = UiState.Error("Failed to add category")
            }
        }
    }
}