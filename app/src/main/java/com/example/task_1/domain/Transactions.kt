package com.example.task_1.domain

import com.example.task_1.data.DataService


class Transactions(private var transactions : MutableList<Transaction> ) {
    fun getTransactions() : List<Transaction> {
        return transactions;
    }
    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }
    fun totalExpenses():Double {
        var sum: Double = 0.0;
        for (transaction in transactions) {
            sum += transaction.money;
        }
        return sum;
    }
    fun indexOfBiggestExpense():Int {
        var indexOfTheBiggest: Int = 0;
        for ( transaction in transactions) {
            if ( transaction.money > transactions[indexOfTheBiggest].money ) {
                indexOfTheBiggest = transactions.indexOf(transaction)
            }
        }
        return indexOfTheBiggest;
    }


    fun getCategories() : List<Category> {
        val set: MutableSet<Category> = mutableSetOf()
        for ( transaction in transactions  ) {
            set.add(transaction.category)
        }
        return set.toList()
    }

    fun categoriesOverview() : List<CategoriesOverview> {
        val categoryExpenses = getCategories().associateWith { 0.0 }.toMutableMap()
        for (transaction in transactions) {
            categoryExpenses.merge(transaction.category, transaction.money, Double::plus)
        }
        return categoryExpenses.map { (category, expense) ->
            CategoriesOverview(category, expense)
        }
    }
}