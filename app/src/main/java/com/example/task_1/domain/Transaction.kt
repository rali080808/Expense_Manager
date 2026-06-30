package com.example.task_1.domain

import java.time.LocalDate

enum class PayMethod (){
    CASH, DEBIT, CREDIT
}
class Transaction(val sender : String,
                  val receiver: String,
                  val money: Double,
                  val currency: String = "€",
                  val date: LocalDate ,
                  val category: Category,
                  val description:String="",
                  val payMethod: PayMethod   )

val MAX_RECEIVER_LENGTH  = 16
val MAX_MONEY_LENGTH  = 16
