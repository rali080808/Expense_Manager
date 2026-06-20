package com.example.task_1.data;

import androidx.compose.animation.core.StartOffsetType.Companion.Delay
import androidx.compose.ui.geometry.Rect
import com.example.task_1.domain.Transaction;
import com.example.task_1.domain.Transactions;


import com.example.task_1.domain.Category;
import com.example.task_1.domain.PayMethod
import kotlinx.coroutines.delay
import java.time.LocalDate

public object DataService : IDataService {
    private  val  categories: MutableList<Category> = mutableListOf(Category("icecream", "🍦"), Category("dresses", "👗"))
    private val transactions : Transactions = Transactions(mutableListOf(
        Transaction("Az", "Ti", 6.0, "€", LocalDate.of(2026, 6, 10), categories[0], "description",
            PayMethod.CASH),

        Transaction("Az", "Ti", 61.0, "€", LocalDate.of(2026, 6, 20), categories[0], "description",
            PayMethod.CASH),

        Transaction("Az", "Ti", 2.0, "€", LocalDate.of(2026, 6, 27), categories[0], "description",
            PayMethod.CASH)))

    override suspend fun addCategory(text: String, icon: String): Result<Category>{
        delay(700)
        if ( categories.containsText(text) ) return Result.failure(IllegalArgumentException("Category $text already exists!"))
        categories.add(Category(text, icon))
        return Result.success(Category(text, icon))
    }

    override suspend fun addTransaction(transaction: Transaction) {
        delay(700)
        transactions.addTransaction(transaction)
    }//TODO how can I make DataService be the only one able to add a transaction to transactions?

    override suspend fun removeCategory(index: Int): List<Category> {
        delay(700)
        categories.removeAt(index)
        return categories;
    }

    override suspend fun editCategory(index: Int, editedCategory: Category): List<Category> {
        delay(700)
        categories[index] = editedCategory
        return categories;
    }
    override suspend fun getCategories() : List<Category> {
        delay(700)
        return categories;
    }
    override suspend fun getTransactions() : Transactions {
        delay(700)
        return transactions;
    }
}

private fun MutableList<Category>.containsText(text: String): Boolean {
    for(category in this) {
        if ( category.text == text) return true;
    }
    return false;
}
