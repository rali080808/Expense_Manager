package com.example.task_1.domain

import com.example.task_1.R
import kotlinx.serialization.Serializable
import java.time.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class PayMethod(val icon: String, val text: Int) {
    CASH("💵", R.string.cash),
    DEBIT("💳", R.string.debit),
    CREDIT("💰", R.string.credit),
    UNKNOWN("💎", R.string.unknown)
}

enum class Currency(val sign: String) {
    EURO("€"),
    DOLLAR("$"),
    UNKNOWN("🪙")
}

@Serializable
data class Transaction(
    val id: Long?,
    val sender: String,
    val receiver: String,
    val money: Double,
    val currency: Currency = Currency.EURO,
    val date: String,
    val categoryID: Long,
    val description: String = "",
    val payMethod: PayMethod
)

val MAX_RECEIVER_LENGTH = 16
val MAX_MONEY_LENGTH = 16
