package com.example.task_1.domain


class Transactions(var transactions : MutableList<Transaction> ) {
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
    fun addTransaction(transaction: Transaction){
        transactions.add(transaction);
    }

    fun categoriesOverview() : List<CategoriesOverview> {
        val categoryExpenses = Category.entries.associateWith { 0.0 }.toMutableMap()
        for (transaction in transactions) {
            categoryExpenses.merge(transaction.category, transaction.money, Double::plus)
        }
        return categoryExpenses.map { (category, expense) ->
            CategoriesOverview(category, expense)
        }
    }
}