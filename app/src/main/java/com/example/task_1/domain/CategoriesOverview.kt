package com.example.task_1.domain

class CategoriesOverview(val categoryID: Int, val expenseOnThisCategory: Double) {
    fun percentage(totalExpenses: Double): Int {
        if ( totalExpenses > 0)
             return (expenseOnThisCategory / totalExpenses * 100 ).toInt()
        return 0
    }
}
