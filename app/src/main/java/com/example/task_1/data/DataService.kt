package com.example.task_1.data;

import androidx.compose.animation.core.StartOffsetType.Companion.Delay
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import com.example.task_1.domain.Transaction;
import com.example.task_1.domain.Transactions;


import com.example.task_1.domain.Category;
import com.example.task_1.domain.PayMethod
import kotlinx.coroutines.delay
import java.time.LocalDate
import kotlin.uuid.Uuid

const val WAIT_TIME: Long = 200
public object DataService : IDataService {
    private  val  categories: MutableMap<Int, Category> = mutableMapOf(
        1 to Category("icecream", "🍦", Color.Yellow),
        2 to Category("dresses", "👗", Color.Blue))
    private var nextCategoryID = 3
    private val transactions : Transactions = Transactions(mutableListOf(
        Transaction("Az", "Ti", 6.0, "€", LocalDate.of(2026, 6, 10), 1, "description",
            PayMethod.CASH),

        Transaction("Az", "Ti", 61.0, "€", LocalDate.of(2026, 6, 20), 2, "description",
            PayMethod.CASH),

        Transaction("Az", "Ti", 2.0, "€", LocalDate.of(2026, 6, 27), 1, "description",
            PayMethod.CASH)))

    override suspend fun addCategory(category: Category): Result<Category>{
        delay(WAIT_TIME)
        if ( categories.containsText(category.text) ) return Result.failure(IllegalArgumentException("Category ${category.text} already exists!"))
        categories[nextCategoryID++] = category
        return Result.success(category)
    }

    override suspend fun addTransaction(transaction: Transaction) {
        delay(WAIT_TIME)
        transactions.addTransaction(transaction)
    }//TODO how can I make DataService be the only one able to add a transaction to transactions?

    override suspend fun removeCategory(id: Int): Map<Int,Category> {
        delay(WAIT_TIME)
        categories.remove(id)
        return categories; //toMap
    }

    override suspend fun editCategory(id: Int, editedCategory: Category): Map<Int,Category> {
        delay(WAIT_TIME)
        categories[id] = editedCategory
        return categories; // toMap
    }
    override suspend fun getCategories() : Map<Int, Category> {
        delay(WAIT_TIME)
        return categories;
    }
    override suspend fun getTransactionsObject() : Transactions {
        delay(WAIT_TIME)
        return transactions;
    }
}

private fun MutableMap<Int,Category>.containsText(text: String): Boolean {
    for((id, category) in this) {
        if ( category.text == text) return true;
    }
    return false;
}
