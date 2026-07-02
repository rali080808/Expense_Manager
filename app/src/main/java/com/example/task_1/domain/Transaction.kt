package com.example.task_1.domain

import java.time.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

enum class PayMethod (){
    CASH, DEBIT, CREDIT
}
class Transaction @OptIn(ExperimentalUuidApi::class) constructor(val sender : String,
                                                                 val receiver: String,
                                                                 val money: Double,
                                                                 val currency: String = "€",
                                                                 val date: LocalDate,
                                                                 val categoryID: Int,
                                                                 val description:String="",
                                                                 val payMethod: PayMethod   )

val MAX_RECEIVER_LENGTH  = 16
val MAX_MONEY_LENGTH  = 16
