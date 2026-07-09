package com.example.task_1.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.util.Predicate.not
import com.example.task_1.data.DataService
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Long?,
    val text: String,
    val icon: String,
    val color: Int,
) {
    fun percentage(totalExpenses: Double, transactions: List<Transaction>): Int {
        if (totalExpenses > 0)
            return (expenseOnThisCategory(transactions) / totalExpenses * 100).toInt()
        return 0
    }

    fun expenseOnThisCategory(transactions: List<Transaction>): Double {
        var expenses: Double = 0.0
        for (transaction in transactions) {
            if (transaction.categoryID == this.id) {
                expenses += transaction.money;
            }
        }
        return expenses
    }
}

const val NoFilter: Long = -1
val ErrorCategory: Category = Category(-1, "Developer bug", "🐜", Color.Red.toArgb())
val MAX_CATEGORY_LENGTH = 16
