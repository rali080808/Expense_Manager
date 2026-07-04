package com.example.task_1.domain

import androidx.compose.ui.graphics.Color
import androidx.core.util.Predicate.not
import com.example.task_1.data.DataService

class Category (val text: String, val icon: String, val color: Color, val expenseOnThisCategory: Double) {
    fun percentage(totalExpenses: Double): Int {
        if ( totalExpenses > 0)
            return (expenseOnThisCategory / totalExpenses * 100 ).toInt()
        return 0
    }
}
const val NoFilter: Int = -1
val ErrorCategory: Category = Category("Developer bug", "🐜", Color.Red,0.0)
val MAX_CATEGORY_LENGTH  = 16
