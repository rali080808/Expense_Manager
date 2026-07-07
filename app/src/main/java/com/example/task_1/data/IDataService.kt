package com.example.task_1.data


import com.example.task_1.domain.Category;
import com.example.task_1.domain.Transaction
import com.example.task_1.ui.TransactionInput

interface IDataService {
    suspend fun addCategory(category: Category)
    suspend fun removeCategory(id: Int): Map<Int, Category>

    suspend fun editCategory(id: Int, editedCategory: Category): Map<Int, Category>

    suspend fun getCategories(): Map<Int, Category>
    suspend fun getTransactions(): List<Transaction>
    suspend fun addTransaction(transaction: TransactionInput)
    suspend fun editTransaction(transaction: Transaction)


}