package com.example.task_1.data.local.mapper

import com.example.task_1.data.local.entity.CategoryEntity
import com.example.task_1.data.local.entity.TransactionEntity
import com.example.task_1.domain.Category
import com.example.task_1.domain.Currency
import com.example.task_1.domain.PayMethod
import com.example.task_1.domain.Transaction

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.id,
        text = this.text,
        icon = this.icon,
        color = this.color
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = this.id ?: 0,
        text = this.text,
        icon = this.icon,
        color = this.color
    )
}

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = this.id,
        sender = this.sender,
        receiver = this.receiver,
        money = this.money,
        currency = this.currency.toCurrency(),
        date = this.date,
        categoryID = this.categoryID,
        description = this.description,
        payMethod = this.payMethod.toPayMethod()
    )
}

fun String.toCurrency(): Currency {
    return Currency.entries.find { it.name == this }
        ?: Currency.UNKNOWN
}

fun String.toPayMethod(): PayMethod {
    return PayMethod.entries.find { it.name == this }
        ?: PayMethod.UNKNOWN
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id ?: 0,
        sender = this.sender,
        receiver = this.receiver,
        money = this.money,
        currency = this.currency.name,
        date = this.date,
        categoryID = this.categoryID,
        description = this.description,
        payMethod = this.payMethod.name,
    )
}