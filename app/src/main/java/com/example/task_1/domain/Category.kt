package com.example.task_1.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.util.Predicate.not
import com.example.task_1.data.DataService
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode

@Serializable
data class Category(
    val id: Long?,
    val text: String,
    val icon: String,
    val color: Int,
) {
    fun percentage(totalExpenses: BigDecimal, transactions: List<Transaction>): Int {
         if (totalExpenses > BigDecimal.ZERO) {
            val categoryExpenses = expenseOnThisCategory(transactions)
            return categoryExpenses
                .divide(totalExpenses, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
                .toInt()
        }
        return 0
    }

    fun expenseOnThisCategory(transactions: List<Transaction>): BigDecimal {
        var expenses: BigDecimal = BigDecimal.ZERO
        for (transaction in transactions) {
            if (transaction.categoryID == this.id) {
                expenses =  expenses.add(BigDecimal(transaction.money))
            }
        }
        return expenses
    }
    companion object{
        val MIN_TEXT_LENGTH = 2
        val MAX_TEXT_LENGTH = 8
    }
}

const val NoFilter: Long = -1
val ErrorCategory: Category = Category(-1, "Developer bug", "🐜", Color.Red.toArgb())
val MAX_CATEGORY_LENGTH = 16
