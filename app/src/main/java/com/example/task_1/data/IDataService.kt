package com.example.task_1.data


import com.example.task_1.domain.Transactions;


import com.example.task_1.domain.Category;
import com.example.task_1.domain.Transaction

interface IDataService {
    suspend fun addCategory(category: Category): Result<Category>
    suspend fun removeCategory(index: Int): List<Category>

    suspend fun editCategory(index: Int, editedCategory: Category): List<Category>

    suspend fun getCategories(): List<Category>
    suspend fun getTransactionsObject(): Transactions
    suspend fun addTransaction(transaction: Transaction)
}