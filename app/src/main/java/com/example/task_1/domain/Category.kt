package com.example.task_1.domain

import android.icu.math.BigDecimal
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.util.Predicate.not
 import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import java.math.RoundingMode
import java.time.LocalDate
import java.time.Month

@Serializable
data class Category(
    val id: Long?,
    val text: String,
    val icon: String,
    val color: Int,
) {
    fun percentage(
        totalExpenses: BigDecimal,
        transactions: List<Transaction>,
        periodFilter: PeriodFilter,
        startDate: String?,
        endDate: String?
    ): Float {
        if (totalExpenses > BigDecimal.ZERO) {
            val categoryExpenses = expenseOnThisCategory(transactions, periodFilter, startDate, endDate)
            return categoryExpenses
                .divide(totalExpenses, 2, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal(100))
                .toFloat()

        }
        return 0.0f
    }

    fun expenseOnThisCategory(
        transactions: List<Transaction>,
        periodFilter: PeriodFilter,
        startDate: String?,
        endDate: String?
    ): BigDecimal {
        var expenses: BigDecimal = BigDecimal.ZERO
        for (transaction in transactions) {
            if (transaction.categoryID == this.id && LocalDate.parse(transaction.date)
                    .isDateInRange(
                        periodFilter,
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate)
                    )
            ) {
                expenses = expenses.add(BigDecimal(transaction.money))
            }
        }
        return expenses
    }

    companion object {
        val MIN_TEXT_LENGTH = 2
        val MAX_TEXT_LENGTH = 16
    }
}

const val NoFilter: Long = -1
val ErrorCategory: Category = Category(-1, "Developer bug", "🐜", Color.Red.toArgb())
val MAX_CATEGORY_LENGTH = 16
