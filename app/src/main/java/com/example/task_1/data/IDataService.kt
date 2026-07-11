package com.example.task_1.data


import com.example.task_1.domain.Category;
import com.example.task_1.domain.Transaction

interface IDataService {
    suspend fun addCategory(category: Category): List<Category>
    suspend fun removeCategory(id: Long): List<Category>
    suspend fun deleteTransaction(id: Long): List<Transaction>
    suspend fun editCategory(id: Long, editedCategory: Category): List<Category>

    suspend fun getCategories(): List<Category>
    suspend fun getTransactions(): List<Transaction>
    suspend fun addTransaction(transaction: Transaction): List<Transaction>
    suspend fun editTransaction(transaction: Transaction): List<Transaction>


}