package com.example.task_1.data;

import androidx.compose.animation.core.StartOffsetType.Companion.Delay
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.util.copy
import com.example.task_1.domain.Transaction;


import com.example.task_1.domain.Category;
import com.example.task_1.domain.Currency
import com.example.task_1.domain.PayMethod
import com.example.task_1.ui.TransactionInput
import kotlinx.coroutines.delay
import java.time.LocalDate
import kotlin.uuid.Uuid

const val WAIT_TIME: Long = 200

public object DataService : IDataService {
    // TODO make categories a list
    private val categories: MutableMap<Int, Category> = mutableMapOf(
        1 to Category("ice cream", "🍦", Color.Magenta.toArgb(), 8.0),
        2 to Category("dresses", "👗", Color.Blue.toArgb(), 61.0)
    )
    private var nextCategoryID = 3
     private val transactions: MutableList<Transaction> =
        mutableListOf(
            Transaction(
                id = "1",
                "Rali",
                "LIDL",
                6.0,
                Currency.EURO,
                LocalDate.of(2026, 6, 10).toString(),
                1,
                "Vanilla ice cream for everyone!",
                PayMethod.CASH
            ),

            Transaction(
                id = "2",
                "Rali",
                "Stradivarius",
                61.0,
                Currency.EURO,
                LocalDate.of(2026, 6, 20).toString(),
                2,
                "Numerous new dresses!",
                PayMethod.CASH
            ),

            Transaction(
                id = "3",
                "Rali",
                "Billa",
                2.0,
                Currency.EURO,
                LocalDate.of(2026, 6, 27).toString(),
                1,
                "Another ice cream!",
                PayMethod.CASH
            )
        )


    override suspend fun addCategory(category: Category) {
        delay(WAIT_TIME)
        categories[nextCategoryID++] = category

    }
    var nextTransactionID = 3;
    override suspend fun addTransaction(transaction: TransactionInput) {
        delay(WAIT_TIME)
        transactions.add(transaction.toTransaction( (nextTransactionID++).toString() ))

        val currentCategory = categories[transaction.categoryID]
            ?: throw IllegalArgumentException("Category ID ${transaction.categoryID} not found")

        categories[transaction.categoryID] = Category(
            text = currentCategory.text,
            icon = currentCategory.icon,
            color = currentCategory.color,
            expenseOnThisCategory = currentCategory.expenseOnThisCategory + transaction.money
        )
    }

    override suspend fun editTransaction( transaction: Transaction) {
        delay(WAIT_TIME)
        // TODO editTransaction
//
//        val oldCategoryID = transactions[index].categoryID
//        val oldMoney = transactions[index].money;
//        val currentCategoryID = transaction.categoryID
//        val currentMoney = transaction.money;
//
//        transactions.editTransaction(index, transaction)
//
//        categories[oldCategoryID]?.let { oldCategory ->
//            categories[oldCategoryID] = oldCategory.copy(
//                expenseOnThisCategory = oldCategory.expenseOnThisCategory - oldMoney
//            )
//        }
//
//        categories[currentCategoryID]?.let { currentCategory ->
//            categories[currentCategoryID] = currentCategory.copy(
//                expenseOnThisCategory = currentCategory.expenseOnThisCategory + currentMoney
//            )
//        }
    }
//TODO how can I make DataService be the only one able to add a transaction to transactions?

    override suspend fun removeCategory(id: Int): Map<Int, Category> {
        delay(WAIT_TIME)
        categories.remove(id)
        return categories; //toMap
    }

    override suspend fun editCategory(id: Int, editedCategory: Category): Map<Int, Category> {
        delay(WAIT_TIME)
        categories[id] = editedCategory
        return categories; // toMap
    }

    override suspend fun getCategories(): Map<Int, Category> {
        delay(WAIT_TIME)
        return categories;
    }

    override suspend fun getTransactions(): List<Transaction> {
        delay(WAIT_TIME)
        return transactions;
    }
}


