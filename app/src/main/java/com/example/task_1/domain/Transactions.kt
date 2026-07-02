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

    fun getBiggestExpense() : Double {
        if (transactions.isEmpty()) return 0.0;

        var indexOfTheBiggest: Int = 0;
        for ( transaction in transactions ) {
            if ( transaction.money > transactions[indexOfTheBiggest].money ) {
                indexOfTheBiggest = transactions.indexOf(transaction)
            }
        }
        return transactions[indexOfTheBiggest].money;
    }



}