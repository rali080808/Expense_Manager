package com.example.task_1.domain

import java.time.LocalDate

class Transaction(val sender : String, val receiver: String, val money: Double, val currency: String,  val date: LocalDate , val category: Category  ) {

}