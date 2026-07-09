package com.example.task_1.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity  (
    @PrimaryKey(autoGenerate = true) val id: Long,
    val sender: String,
    val receiver: String,
    val money: Double,
    val currency: String,
    val date: String,
    val categoryID: Long,
    val description: String,
    val payMethod: String
)