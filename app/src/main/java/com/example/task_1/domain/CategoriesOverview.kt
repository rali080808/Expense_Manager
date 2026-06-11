package com.example.task_1.domain

class CategoriesOverview(val category: Category, val expenseOnThisCategory: Double) {
    fun percentage(totalExpenses: Double): Int {
        return (expenseOnThisCategory / totalExpenses * 100 ).toInt();
    }
}
