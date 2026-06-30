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
const val WAIT_TIME: Long = 200
public object DataService : IDataService {
    private  val  categories: MutableList<Category> = mutableListOf(Category("icecream", "🍦", Color.Yellow), Category("dresses", "👗", Color.Blue))
    private val transactions : Transactions = Transactions(mutableListOf(
        Transaction("Az", "Ti", 6.0, "€", LocalDate.of(2026, 6, 10), categories[0], "description",
            PayMethod.CASH),

        Transaction("Az", "Ti", 61.0, "€", LocalDate.of(2026, 6, 20), categories[0], "description",
            PayMethod.CASH),

        Transaction("Az", "Ti", 2.0, "€", LocalDate.of(2026, 6, 27), categories[0], "description",
            PayMethod.CASH)))

    override suspend fun addCategory(category: Category): Result<Category>{
        delay(700)
        if ( categories.containsText(category.text) ) return Result.failure(IllegalArgumentException("Category ${category.text} already exists!"))
        categories.add(Category(category.text, category.icon, category.color))
        return Result.success(Category(category.text, category.icon, category.color))
    }

    override suspend fun addTransaction(transaction: Transaction) {
        delay(WAIT_TIME)
        transactions.addTransaction(transaction)
    }//TODO how can I make DataService be the only one able to add a transaction to transactions?

    override suspend fun removeCategory(index: Int): List<Category> {
        delay(WAIT_TIME)
        categories.removeAt(index)
        return categories;
    }

    override suspend fun editCategory(index: Int, editedCategory: Category): List<Category> {
        delay(WAIT_TIME)
        categories[index] = editedCategory
        return categories;
    }
    override suspend fun getCategories() : List<Category> {
        delay(WAIT_TIME)
        return categories;
    }
    override suspend fun getTransactionsObject() : Transactions {
        delay(WAIT_TIME)
        return transactions;
    }
}

private fun MutableList<Category>.containsText(text: String): Boolean {
    for(category in this) {
        if ( category.text == text) return true;
    }
    return false;
}
