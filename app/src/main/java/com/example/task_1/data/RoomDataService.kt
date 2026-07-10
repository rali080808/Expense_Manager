package com.example.task_1.data

import com.example.task_1.data.local.dao.CategoryDao
import com.example.task_1.data.local.dao.TransactionDao
import com.example.task_1.data.local.mapper.toDomain
import com.example.task_1.data.local.mapper.toEntity
import com.example.task_1.domain.Category
import com.example.task_1.domain.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class RoomDataService(
    private val categoryDao: CategoryDao,
    private val transactionDao: TransactionDao
) : IDataService {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

     private val categoriesState: StateFlow<List<Category>> = categoryDao.getCategories()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(
            scope = serviceScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

     private val transactionsState: StateFlow<List<Transaction>> = transactionDao.getTransactions()
        .map { entities -> entities.map { it.toDomain() } }
        .stateIn(
            scope = serviceScope,
            started = SharingStarted.Eagerly,
            initialValue = emptyList()
        )

     override suspend fun getCategories(): List<Category> = categoriesState.value

    override suspend fun getTransactions(): List<Transaction> = transactionsState.value

    override suspend fun addCategory(category: Category): Unit = withContext(Dispatchers.IO) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun addTransaction(transaction: Transaction): Unit = withContext(Dispatchers.IO) {
        transactionDao.insertTransaction(transaction.toEntity())
    }

    override suspend fun editTransaction(transaction: Transaction): Unit = withContext(Dispatchers.IO) {
        transactionDao.updateTransaction(transaction.toEntity())
    }

    override suspend fun removeCategory(id: Long): List<Category> = withContext(Dispatchers.IO) {
        categoryDao.deleteCategoryById(id)
        return@withContext categoriesState.value
    }

    override suspend fun editCategory(id: Long, editedCategory: Category): List<Category> = withContext(Dispatchers.IO) {
        categoryDao.updateCategory(editedCategory.toEntity())
        return@withContext categoriesState.value
    }

     fun clear() {
        serviceScope.cancel()
    }
}
