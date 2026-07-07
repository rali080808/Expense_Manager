package com.example.task_1.ui

import com.example.task_1.domain.Currency
import com.example.task_1.domain.PayMethod
import com.example.task_1.domain.Transaction

data class TransactionInput(
    val sender: String,
    val receiver: String,
    val money: Double,
    val currency: Currency = Currency.EURO,
    val date: String,
    val categoryID: Int,
    val description: String = "",
    val payMethod: PayMethod
) {
    fun toTransaction(id: String): Transaction {
        return Transaction(
            id = id,
            sender = this.sender,
            receiver = this.receiver,
            money = this.money,
            currency = this.currency,
            date = this.date,
            categoryID = this.categoryID,
            description = this.description,
            payMethod = this.payMethod
        )
    }
}