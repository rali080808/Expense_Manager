package com.example.task_1.data


import com.example.task_1.domain.Transactions;


import com.example.task_1.domain.Category;
import com.example.task_1.domain.Transaction

interface IDataService {
    suspend fun addCategory(category: Category): Result<Category>
    suspend fun removeCategory(id: Int): Map<Int,Category>

    suspend fun editCategory(id: Int, editedCategory: Category): Map<Int,Category>

    suspend fun getCategories(): Map<Int,Category>
    suspend fun getTransactionsObject(): Transactions
    suspend fun addTransaction(transaction: Transaction)
}